# FitnessPlanner-App

<p>
    <img src="https://user-images.githubusercontent.com/25181517/117201470-f6d56780-adec-11eb-8f7c-e70e376cfd07.png" width="45" height="45">
    <img src="https://user-images.githubusercontent.com/25181517/186711335-a3729606-5a78-4496-9a36-06efcc74f800.png" width="45" height="45">
    <img src="https://user-images.githubusercontent.com/25181517/183897015-94a058a6-b86e-4e42-a37f-bf92061753e5.png" width="45" height="45">
    <img src="https://user-images.githubusercontent.com/25181517/117533873-484d4480-afef-11eb-9fad-67c8605e3592.png" width="45" height="45">
    <img src="https://user-images.githubusercontent.com/25181517/183892181-ad32b69e-3603-418c-b8e7-99e976c2a784.png" width="45" height="45">
    <img src="https://user-images.githubusercontent.com/25181517/183896128-ec99105a-ec1a-4d85-b08b-1aa1620b2046.png" width="45" height="45">
</p>

## Table of Contents

- [Overview](#overview)
- [Microservices](#microservices)
- [Frontend](#frontend)

## Overview

This project is a Fitness Management System consisting of three microservices. The User Service handles user authentication and authorization, providing users with tokens for interactions with the system. The Reservation Service allows clients to browse and search for available trainings, while gym managers can input data about their gym. The Notification Service sends emails for account activations, successfully scheduled training sessions and reminders before each session.

## Microservices

### Microservice 1 (User Service)
- User Types: The system supports different user types: admin, client, and gym manager.
- Registration: Clients and managers have separate registration routes.
- Login: Users log in with email and password, receiving a JWT upon successful authentication.
- Application Access Restriction: Admin can ban/unban user access to the application.
- Profile Editing: Users can edit their profile parameters, excluding membership card number and scheduled training count.

### Microservice 2 (Reservation Service)
- Gym Management: Gym managers can input and update data about their gyms, including name, description, number of trainers etc...
- Listing Available Trainings: Users can view available training slots based on type, individual/group, day, and start time.
- Training Session Booking: Clients can book available training slots, triggering an email confirmation. Retry pattern implemented for network issues.
- Canceling Booked Training: Clients and gym managers can cancel booked training sessions, updating availability and notifying the User Service.
- Loyalty Program: Gym managers can define loyalty rewards, e.g., a free session after every 10th booked sessions.

### Microservice 3 (Notification Service)
- Notification Types: Admin defines notification types with associated text and parameters.
  - Activation Email Sending: Handles activation email requests from the User Service.
  - Password Change Email Sending.
  - Successful Training Booking Notification: Sent to the client and gym manager.
  - Training Cancellation Notification: Sent to the client(s) and gym manager.
  - Reminder Notification 24h Before Training.
- Notification Archive: All sent notifications stored in the database, with filtering options.
- Asynchronous Notification Sending: Implemented via message broker to ensure loose coupling.

### Eureka (Service Discovery)
- Dynamic Service Registration: Each service registers itself with Eureka, providing a dynamic and up-to-date service registry.
- Service Discovery: Eureka allows services to locate and communicate with each other without hardcoding URLs or IP addresses.
- Load Balancing: Eureka provides load balancing capabilities, distributing incoming requests among available instances of a service.

### API Gateway
- Routing: Routing requests to the appropriate service based on predefined rules.
- Load Balancing: Distributing incoming requests across multiple instances of a service to ensure optimal resource utilization.

## Frontend

### Frontend (React)
The frontend of the Fitness Planner System is made using React, offering an engaging and user-friendly interface for interaction with the microservices.
