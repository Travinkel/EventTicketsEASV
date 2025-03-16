### **FIGMA STYLE GUIDE**
📌 *Event Ticket System - JavaFX Fluent Design Standards*

---

## **1. Introduction**
This document defines the design system for the **Event Ticket System** to ensure **consistency across JavaFX (desktop), web, and mobile platforms** while following a feasible and maintainable Fluent Design approach in JavaFX.

---

## **2. Color Palette 🎨**
| Name         | HEX       | RGB             | Use Case                      |
|--------------|-----------|-----------------|-------------------------------|
| Primary Blue | `#009FE3` | `0, 159, 227`   | Main buttons, links, accents  |
| Dark Gray    | `#333333` | `51, 51, 51`    | Headings, labels              |
| Light Gray   | `#DADADA` | `218, 218, 218` | Borders, inactive elements    |
| Background   | `#FAFAFA` | `250, 250, 250` | App background                |
| White        | `#FFFFFF` | `255, 255, 255` | Cards, form fields            |

*📌 Ensure sufficient contrast for readability and accessibility (WCAG AA+ level).*

---

## **3. Typography 📝**
| Font Family       | Weight     | Use Case               |
|-------------------|------------|------------------------|
| Segoe UI Semibold | `600`      | Buttons, key actions   |
| Segoe UI Light    | `300`      | Secondary text         |
| Segoe UI Regular  | `400`      | Body text, form inputs |

**Font Sizes**
- **Heading 1:** `28px`
- **Heading 2:** `24px`
- **Body:** `16px`
- **Small Text:** `14px`

---

## **4. UI Components 📦**
### **4.1 Cards**
- **Login Card**
    - `width: 420px`
    - `background: rgba(245, 245, 245, 1.0)` (no transparency for better readability)
    - `border-radius: 12px`
    - `border: 1px solid rgba(220, 220, 220, 0.6)`
    - `box-shadow: 0px 10px 25px rgba(0,0,0,0.2)`

- **Event Details Card**
    - `width: 600px`
    - `background: white`
    - `border-radius: 8px`
    - `padding: 20px`

---

## **5. Spacing & Layout 📏**
**Margin & Padding**
- Default margin: `16px`
- Card inner padding: `30px`
- Button spacing: `12px`
- Section gaps: `24px`

**Grid System**
- **JavaFX (desktop)**: Uses **VBox/HBox with `spacing: 15px`**
- **Web/Mobile:** Uses **CSS Flexbox/Grid with 12-column layout**

---

## **6. Buttons 🎛️**
| Type         | Background    | Text Color | Border  | Effect             |
|--------------|---------------|------------|---------|--------------------|
| Primary      | `#009FE3`     | `#FFFFFF`  | None    | Hover: Darker blue |
| Secondary    | `transparent` | `#009FE3`  | None    | Hover: Underline   |
| Disabled     | `#DADADA`     | `#FFFFFF`  | None    | None               |

**Corner Radius:** `4px`  
**Padding:** `10px 20px`

---

## **7. Icons 🖼️**
Icons should be **24x24px** and use **Lucide Icons** for web & mobile.  
For JavaFX, use **ImageView with scalable SVGs**.

---

## **8. Cross-Platform Guidelines 🌍**
**JavaFX Desktop**
- Use **VBox, HBox, and AnchorPane** for UI structure.
- Implement **CSS for theming** instead of inline styling.
- Use **solid color backgrounds instead of transparency effects** for better compatibility.
- Apply **shadows and gradients sparingly** for a modern look.

**Web & Mobile**
- Use **TailwindCSS** or **Bootstrap** for responsiveness.
- Implement **Material Design-style buttons & inputs**.
- Use **flexbox/grid** for layout consistency.

---

## **9. Changelog 📜**
Keep track of all Figma updates here.

| Date       | Change                                      | Author   |
|------------|---------------------------------------------|----------|
| 2025-03-08 | Initial style guide created                 | You      |
| 2025-03-16 | Removed blur effects, improved contrast     | You      |

---