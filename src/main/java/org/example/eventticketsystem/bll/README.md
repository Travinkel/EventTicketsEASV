# 🧠 Business Logic Layer (bll)

This package contains all service-level logic that connects the GUI to the database.
Each service implements domain-specific business rules.

## ✅ Responsibilities

- Coordinate between DAOs and controllers
- Validate, transform, and prepare data for persistence or display
- Enforce system-level rules (e.g., access control)

## 🧠 Learning Structure

- UserService.java → Auth + registration
- EventService.java → Event lifecycle (create, update, delete)
- TicketService.java → Ticket booking, validation, PDF gen

Use SLF4J logging and full Javadoc for all public methods.
