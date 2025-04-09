# 📚 Code Documentation & Commenting Guidelines

This guide defines how to document code for clarity, maintainability, and learning—especially when using AI assistance.

---

## ✍️ Commenting Strategy by Layer

### 🧱 Class-Level (Javadoc)

- Purpose of the class
- Design pattern (Controller, DAO, Service, etc.)
- Dependencies via `@link`

### 🔧 Method-Level (Javadoc)

- Describe business logic
- Use `@param`, `@return`, `@throws`
- Mention side-effects (e.g., DB writes)

### 🧩 Inline Comments

- Explain *why*, not *what*
- Add value beyond the syntax

```java
// 📌 Sanitize input before using in DB query
String clean = sanitize(input);
