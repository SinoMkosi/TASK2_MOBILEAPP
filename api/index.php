<?php
// ZAS Petroleum API - single entry point. Usage: index.php?route=login
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Access-Control-Allow-Methods: GET, POST, PUT, OPTIONS');
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') { exit; }

// ---------- helpers ----------
function env(string $k, $d = null) { $v = getenv($k); return $v === false ? $d : $v; }
function out($data, int $code = 200) { http_response_code($code); echo json_encode($data); exit; }
function body(): array { return json_decode(file_get_contents('php://input'), true) ?? []; }
function b64u(string $d): string { return rtrim(strtr(base64_encode($d), '+/', '-_'), '='); }
function b64d(string $d): string { return base64_decode(strtr($d, '-_', '+/')); }

function db(): PDO {
    static $pdo = null;
    if ($pdo) return $pdo;
    $opts = [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION, PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC];
    if (env('DB_SSL', '1') === '1') {
        $opts[PDO::MYSQL_ATTR_SSL_CA] = '/etc/ssl/certs/ca-certificates.crt';
        $opts[PDO::MYSQL_ATTR_SSL_VERIFY_SERVER_CERT] = false;
    }
    $dsn = 'mysql:host=' . env('DB_HOST') . ';port=' . env('DB_PORT', '3306') . ';dbname=' . env('DB_NAME') . ';charset=utf8mb4';
    return $pdo = new PDO($dsn, env('DB_USER'), env('DB_PASS'), $opts);
}

// ---------- tokens ----------
function jwt_sign(array $p): string {
    $h = b64u(json_encode(['alg' => 'HS256', 'typ' => 'JWT']));
    $b = b64u(json_encode($p));
    return "$h.$b." . b64u(hash_hmac('sha256', "$h.$b", env('JWT_SECRET'), true));
}
function jwt_verify(string $t): ?array {
    $p = explode('.', $t);
    if (count($p) !== 3) return null;
    $exp = b64u(hash_hmac('sha256', "$p[0].$p[1]", env('JWT_SECRET'), true));
    if (!hash_equals($exp, $p[2])) return null;
    $d = json_decode(b64d($p[1]), true);
    return ($d && ($d['exp'] ?? 0) >= time()) ? $d : null;
}
function token_for(array $u): string {
    return jwt_sign(['uid' => (int)$u['user_id'], 'role' => $u['role'], 'exp' => time() + 8 * 3600]);
}
function auth(array $roles = []): array {
    $h = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
    if (!preg_match('/Bearer\s+(.+)/i', $h, $m)) out(['error' => 'Missing token'], 401);
    $d = jwt_verify($m[1]);
    if (!$d) out(['error' => 'Invalid or expired token'], 401);
    if ($roles && !in_array($d['role'], $roles, true)) out(['error' => 'Not allowed for your role'], 403);
    return $d;
}

// Verifies a Firebase (Google sign-in) ID token
function verify_firebase(string $idToken): ?array {
    $p = explode('.', $idToken);
    if (count($p) !== 3) return null;
    $header = json_decode(b64d($p[0]), true);
    $claims = json_decode(b64d($p[1]), true);
    if (($header['alg'] ?? '') !== 'RS256') return null;
    $certs = json_decode(@file_get_contents('https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com'), true);
    $cert = $certs[$header['kid'] ?? ''] ?? null;
    if (!$cert) return null;
    $pid = env('FIREBASE_PROJECT_ID');
    $ok = openssl_verify("$p[0].$p[1]", b64d($p[2]), $cert, OPENSSL_ALGO_SHA256);
    if ($ok !== 1 || ($claims['aud'] ?? '') !== $pid || ($claims['iss'] ?? '') !== "https://securetoken.google.com/$pid"
        || ($claims['exp'] ?? 0) < time() || empty($claims['email'])) return null;
    return $claims;
}

// ---------- routes ----------
$route = $_GET['route'] ?? '';
$method = $_SERVER['REQUEST_METHOD'];

try {
switch ($route) {

case 'health':
    db()->query('SELECT 1');
    out(['status' => 'ok']);

case 'register': // POST
    $b = body();
    $name = trim($b['full_name'] ?? '');
    $email = strtolower(trim($b['email'] ?? ''));
    $pw = $b['password'] ?? '';
    if ($name === '' || !filter_var($email, FILTER_VALIDATE_EMAIL)) out(['error' => 'Enter your name and a valid email'], 400);
    if (strlen($pw) < 8) out(['error' => 'Password must be at least 8 characters'], 400);
    $s = db()->prepare('SELECT 1 FROM users WHERE email = ?'); $s->execute([$email]);
    if ($s->fetch()) out(['error' => 'Email is already registered'], 409);
    $hash = password_hash($pw, PASSWORD_BCRYPT);
    db()->prepare("INSERT INTO users (full_name, email, phone, password_hash, auth_provider, role) VALUES (?,?,?,?, 'local','customer')")
        ->execute([$name, $email, $b['phone'] ?? null, $hash]);
    out(['message' => 'Registered successfully', 'user_id' => (int)db()->lastInsertId()], 201);

case 'login': // POST
    $b = body();
    $email = strtolower(trim($b['email'] ?? ''));
    $s = db()->prepare('SELECT * FROM users WHERE email = ?'); $s->execute([$email]);
    $u = $s->fetch();
    if (!$u || !$u['password_hash'] || !password_verify($b['password'] ?? '', $u['password_hash']))
        out(['error' => 'Invalid email or password'], 401);
    out(['token' => token_for($u), 'user_id' => (int)$u['user_id'], 'full_name' => $u['full_name'], 'role' => $u['role']]);

case 'sso': // POST {id_token} from Firebase Google sign-in
    $claims = verify_firebase(body()['id_token'] ?? '');
    if (!$claims) out(['error' => 'Invalid SSO token'], 401);
    $email = strtolower($claims['email']);
    $s = db()->prepare('SELECT * FROM users WHERE email = ?'); $s->execute([$email]);
    $u = $s->fetch();
    if (!$u) {
        db()->prepare("INSERT INTO users (full_name, email, auth_provider, role) VALUES (?,?, 'google','customer')")
            ->execute([$claims['name'] ?? $email, $email]);
        $s->execute([$email]); $u = $s->fetch();
    }
    out(['token' => token_for($u), 'user_id' => (int)$u['user_id'], 'full_name' => $u['full_name'], 'role' => $u['role']]);

case 'settings': // GET / PUT
    $me = auth();
    if ($method === 'PUT') {
        $b = body();
        $theme = in_array($b['theme'] ?? 'light', ['light', 'dark'], true) ? $b['theme'] : 'light';
        $name = trim($b['full_name'] ?? '');
        if ($name === '') out(['error' => 'Name cannot be empty'], 400);
        db()->prepare('UPDATE users SET full_name=?, phone=?, theme=?, notifications_on=? WHERE user_id=?')
            ->execute([$name, $b['phone'] ?? null, $theme, !empty($b['notifications_on']) ? 1 : 0, $me['uid']]);
        out(['message' => 'Settings saved']);
    }
    $s = db()->prepare('SELECT user_id, full_name, email, phone, theme, notifications_on, role FROM users WHERE user_id=?');
    $s->execute([$me['uid']]); out($s->fetch());

case 'services': // GET
    auth(); out(db()->query('SELECT * FROM services ORDER BY name')->fetchAll());

case 'vehicles': // GET / POST
    $me = auth();
    if ($method === 'POST') {
        $b = body();
        foreach (['make', 'model', 'year', 'registration'] as $f) if (empty($b[$f])) out(['error' => "$f is required"], 400);
        if (!ctype_digit((string)$b['year']) || $b['year'] < 1980 || $b['year'] > (int)date('Y') + 1) out(['error' => 'Enter a valid year'], 400);
        try {
            db()->prepare('INSERT INTO vehicles (user_id, make, model, year, registration) VALUES (?,?,?,?,?)')
                ->execute([$me['uid'], $b['make'], $b['model'], $b['year'], strtoupper($b['registration'])]);
        } catch (PDOException $e) { out(['error' => 'That registration already exists'], 409); }
        out(['vehicle_id' => (int)db()->lastInsertId()], 201);
    }
    $s = db()->prepare('SELECT * FROM vehicles WHERE user_id=?'); $s->execute([$me['uid']]); out($s->fetchAll());

case 'slots': // GET ?date=YYYY-MM-DD (optional)
    auth();
    $date = $_GET['date'] ?? null;
    $sql = "SELECT t.slot_id, t.slot_date, t.slot_time, t.capacity,
              (SELECT COUNT(*) FROM bookings b WHERE b.slot_id=t.slot_id AND b.status<>'Cancelled') AS booked
            FROM time_slots t WHERE " . ($date ? 't.slot_date = ?' : 't.slot_date >= CURDATE()') . ' ORDER BY t.slot_date, t.slot_time';
    $s = db()->prepare($sql); $s->execute($date ? [$date] : []);
    $rows = $s->fetchAll();
    foreach ($rows as &$r) $r['available'] = $r['booked'] < $r['capacity'];
    out($rows);

case 'bookings': // GET / POST
    $me = auth();
    if ($method === 'POST') {
        $b = body();
        foreach (['vehicle_id', 'service_id', 'slot_id'] as $f) if (empty($b[$f])) out(['error' => "$f is required"], 400);
        $pdo = db(); $pdo->beginTransaction();
        $s = $pdo->prepare('SELECT * FROM time_slots WHERE slot_id=? FOR UPDATE'); $s->execute([$b['slot_id']]);
        $slot = $s->fetch();
        if (!$slot) { $pdo->rollBack(); out(['error' => 'Slot not found'], 404); }
        if ($slot['slot_date'] < date('Y-m-d')) { $pdo->rollBack(); out(['error' => 'Only future dates are allowed'], 400); }
        $s = $pdo->prepare('SELECT 1 FROM vehicles WHERE vehicle_id=? AND user_id=?'); $s->execute([$b['vehicle_id'], $me['uid']]);
        if (!$s->fetch()) { $pdo->rollBack(); out(['error' => 'Vehicle not found'], 404); }
        $s = $pdo->prepare("SELECT COUNT(*) FROM bookings WHERE slot_id=? AND status<>'Cancelled'"); $s->execute([$b['slot_id']]);
        if ($s->fetchColumn() >= $slot['capacity']) { $pdo->rollBack(); out(['error' => 'That time slot is full'], 409); }
        $pdo->prepare("INSERT INTO bookings (user_id, vehicle_id, service_id, slot_id, status, special_instructions) VALUES (?,?,?,?, 'Pending', ?)")
            ->execute([$me['uid'], $b['vehicle_id'], $b['service_id'], $b['slot_id'], $b['special_instructions'] ?? null]);
        $id = (int)$pdo->lastInsertId(); $pdo->commit();
        out(['booking_id' => $id, 'status' => 'Pending'], 201);
    }
    $sql = 'SELECT b.booking_id, b.status, b.special_instructions, s.name AS service, t.slot_date, t.slot_time,
                   v.make, v.model, v.registration, u.full_name AS customer
            FROM bookings b JOIN services s ON s.service_id=b.service_id JOIN time_slots t ON t.slot_id=b.slot_id
            JOIN vehicles v ON v.vehicle_id=b.vehicle_id JOIN users u ON u.user_id=b.user_id';
    if ($me['role'] === 'customer') { $st = db()->prepare("$sql WHERE b.user_id=? ORDER BY t.slot_date DESC"); $st->execute([$me['uid']]); }
    else { $st = db()->query("$sql ORDER BY t.slot_date DESC"); }
    out($st->fetchAll());

case 'cancel_booking': // POST {booking_id}
    $me = auth();
    $id = body()['booking_id'] ?? 0;
    $s = db()->prepare("UPDATE bookings SET status='Cancelled' WHERE booking_id=? AND user_id=? AND status IN ('Pending','Confirmed')");
    $s->execute([$id, $me['uid']]);
    if (!$s->rowCount()) out(['error' => 'Booking cannot be cancelled'], 400);
    out(['message' => 'Booking cancelled']);

case 'booking_status': // POST {booking_id, status} technician/admin
    auth(['technician', 'admin']);
    $b = body();
    if (!in_array($b['status'] ?? '', ['Pending', 'Confirmed', 'Completed', 'Cancelled'], true)) out(['error' => 'Invalid status'], 400);
    db()->prepare('UPDATE bookings SET status=? WHERE booking_id=?')->execute([$b['status'], $b['booking_id'] ?? 0]);
    out(['message' => 'Status updated']);

case 'notes': // POST {booking_id, note} technician/admin
    $me = auth(['technician', 'admin']);
    $b = body();
    if (trim($b['note'] ?? '') === '' || empty($b['booking_id'])) out(['error' => 'booking_id and note are required'], 400);
    db()->prepare('INSERT INTO service_notes (booking_id, technician_id, note) VALUES (?,?,?)')->execute([$b['booking_id'], $me['uid'], $b['note']]);
    out(['message' => 'Note saved'], 201);

case 'feedback': // POST {booking_id, rating, comment}
    $me = auth(['customer']);
    $b = body();
    $rating = (int)($b['rating'] ?? 0);
    if ($rating < 1 || $rating > 5) out(['error' => 'Rating must be 1 to 5'], 400);
    $s = db()->prepare("SELECT 1 FROM bookings WHERE booking_id=? AND user_id=? AND status='Completed'");
    $s->execute([$b['booking_id'] ?? 0, $me['uid']]);
    if (!$s->fetch()) out(['error' => 'You can only rate your completed bookings'], 400);
    db()->prepare('INSERT INTO feedback (booking_id, user_id, rating, comment) VALUES (?,?,?,?)')->execute([$b['booking_id'], $me['uid'], $rating, $b['comment'] ?? null]);
    out(['message' => 'Thank you for your feedback'], 201);

case 'users': // GET admin only
    auth(['admin']);
    out(db()->query('SELECT user_id, full_name, email, role, auth_provider, created_at FROM users')->fetchAll());

default:
    out(['error' => 'Unknown route'], 404);
}
} catch (Throwable $e) {
    error_log('ZAS API error: ' . $e->getMessage()); // visible in Azure Log stream
    out(['error' => 'Server error'], 500);
}
