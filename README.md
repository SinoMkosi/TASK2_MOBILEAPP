# ZAS Petroleum Vehicle Services & Online Booking System (Mobile App)

## Youtube Link:
https://youtube.com/shorts/R_3uqHir670?si=LRpVQ1Jk55iP3t0j

An Android app for ZAS Petroleum customers to register, log in, and book, view, reschedule and cancel vehicle service appointments. It replaces the garage's walk-in, manual booking process and works alongside the ZAS Petroleum website, which staff use to manage bookings.

**Client:** ZAS Petroleum
**Group:** Zasty
**Module:** XISD6329 – Work Integrated Learning (WIL), Task 2

## Team

| Name | Student No. | Role |
|---|---|---|
| Sinoxolo Mkosi | ST10445407 | Team Leader – GitHub repo, CI/CD pipeline, automated tests, documentation |
| Banathi Maholwana | ST10177115 | Technical Lead / Risk Manager – backend, database, authentication, Azure DevOps |
| Sinako Gulwa | ST10449972 | Mobile Development – program modules, input validation, settings, system testing |
| Achuma Madubula | ST10455989 | Design / Documentation – UI screens, help system, user documentation |

## Features

- Register and log in (passwords are hashed, never stored as plain text)
- Single sign-on (SSO) with [SSO PROVIDER]
- Book a service: choose a vehicle, service type, future date and an available time slot
- My Bookings: view all, upcoming and past bookings, with cancel and reschedule options
- Service history and profile (personal and vehicle details)
- Settings the user can change in the app
- Input validation that handles invalid input without crashing
- In-app help and user documentation

## Technology

| Area | Tool |
|---|---|
| Mobile app | Kotlin, Android Studio |
| Database | Azure SQL |
| API | [API TECHNOLOGY AND HOSTING] |
| Authentication | Password hashing and SSO ([SSO PROVIDER]) |
| Version control | GitHub (branches and Pull Requests) |
| CI/CD | GitHub Actions |
| Project management | Azure DevOps – [AZURE DEVOPS LINK] |

## How to run the app

1. Clone the repo: `git clone https://github.com/SinoMkosi/TASK2_MOBILEAPP.git`
2. Open the project in Android Studio and wait for Gradle to finish syncing.
3. On your Android phone, turn on Developer options and USB debugging.
4. Connect the phone to your computer with a USB cable.
5. Select your phone in the device list and click the green Run button.

## How to run the tests

- **Android Studio:** right-click the `test` folder and choose **Run Tests**.
- **GitHub:** the tests run automatically on every push. See the **Actions** tab.

## DevOps

The project follows the DevOps lifecycle: Plan, Code, Build, Test, Release, Deploy, Operate, Monitor.

- **Plan:** tasks and sprints are tracked on Azure DevOps.
- **Code:** each member works on a personal branch (`banathi-backend`, `sinako-logic`, `achuma-ui`, `sinoxolo-devops`).
- **Build and Test:** a GitHub Actions workflow builds the app and runs the unit tests on every push.
- **Release:** work is merged into `main` through reviewed Pull Requests.
- **Monitor:** lecturer feedback and team retrospectives feed into the next sprint.

## Submission notes

- No zip files are committed to this repo.
- Every member commits under their own GitHub account.
- Passwords and connection strings are never committed.

## Documentation

- Task 1 document (Updated Project Plan, Site Map, Wireframes): see the `docs` folder
- User guide: see the `docs` folder

## Demo video

[VIDEO LINK]
