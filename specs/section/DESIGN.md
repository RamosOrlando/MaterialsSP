---
name: Constructo Pro
colors:
  surface: '#fbf8fe'
  surface-dim: '#dcd9de'
  surface-bright: '#fbf8fe'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f2f8'
  surface-container: '#f0edf2'
  surface-container-high: '#eae7ed'
  surface-container-highest: '#e4e1e7'
  on-surface: '#1b1b1f'
  on-surface-variant: '#454652'
  inverse-surface: '#303034'
  inverse-on-surface: '#f3f0f5'
  outline: '#757684'
  outline-variant: '#c5c5d4'
  surface-tint: '#4355b9'
  primary: '#011d86'
  on-primary: '#ffffff'
  primary-container: '#24389c'
  on-primary-container: '#9dabff'
  inverse-primary: '#bac3ff'
  secondary: '#5a5c76'
  on-secondary: '#ffffff'
  secondary-container: '#dcddfc'
  on-secondary-container: '#5f617b'
  tertiary: '#412238'
  on-tertiary: '#ffffff'
  tertiary-container: '#5a384f'
  on-tertiary-container: '#cfa3be'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dee0ff'
  primary-fixed-dim: '#bac3ff'
  on-primary-fixed: '#00105b'
  on-primary-fixed-variant: '#283ca0'
  secondary-fixed: '#dfe0ff'
  secondary-fixed-dim: '#c3c4e2'
  on-secondary-fixed: '#171a30'
  on-secondary-fixed-variant: '#43455e'
  tertiary-fixed: '#ffd7ee'
  tertiary-fixed-dim: '#e8b9d5'
  on-tertiary-fixed: '#2e1126'
  on-tertiary-fixed-variant: '#5e3c53'
  background: '#fbf8fe'
  on-background: '#1b1b1f'
  surface-variant: '#e4e1e7'
  success-container: '#dcfce7'
  on-success-container: '#166534'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 57px
    fontWeight: '400'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
  title-lg:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
  title-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0.15px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  edge-margin: 16px
  gutter: 16px
---

## Brand & Style

Constructo Pro is a **Corporate / Modern** design system tailored for the construction and industrial sectors. It emphasizes reliability, precision, and efficiency. The aesthetic is clean and systematic, utilizing a structured layout that feels professional and trustworthy. It balances utility with high readability, ensuring that critical data (like pricing and inventory) is the focal point. The interface uses a refined Material-inspired approach with soft tonal layering to differentiate content without overwhelming the user.

## Colors

The color palette is anchored by a deep **Navy Primary (#24389c)**, symbolizing stability and corporate professionalism. 

- **Primary:** Used for key actions, brand identity, and focal points.
- **Surface Palette:** Employs a sophisticated range of cool grays with subtle violet undertones (Surface Container tiers) to create a clear information hierarchy without heavy borders.
- **Semantic Accents:** Utilizes high-contrast containers for status indicators (e.g., Red for price increases, Green for decreases) to provide instant visual feedback.
- **Background:** A very light tinted off-white (#fbf8fe) keeps the UI feeling fresh and airy.

## Typography

The system relies exclusively on **Inter**, a highly legible, utilitarian typeface designed for screens. 

- **Headlines:** Use a lighter weight (400) for large display text to maintain elegance.
- **Titles:** Use bold weights (700) for primary headers to establish a strong hierarchy.
- **Data Display:** Labels and status chips use medium weights (500) and increased letter spacing to ensure readability at small sizes.
- **Consistency:** Line heights are strictly adhered to for a predictable vertical rhythm.

## Layout & Spacing

The layout follows a **Fluid Grid** model designed for high density and adaptability.

- **Mobile:** Uses a single-column stack with 16px horizontal margins (`edge-margin`). Horizontal scrolling (snap-scroll) is used for featured items to maximize vertical real estate.
- **Desktop/Tablet:** Transitions to a multi-column responsive grid (up to 4 columns) with a 16px gutter.
- **Rhythm:** An 8px-based spacing system governs all internal padding and margins, ensuring a consistent and harmonious layout.

## Elevation & Depth

Hierarchy is established primarily through **Tonal Layers** rather than heavy shadows.

- **Surface Levels:** The background uses the lightest surface, while interactive elements like cards and search bars use slightly darker container colors (`surface-container-low` or `high`) to appear "raised."
- **Soft Interactivity:** Hover states utilize very low-opacity overlays (e.g., `primary` at 5% opacity) to provide tactile feedback without visual clutter.
- **Shadows:** Only used sparingly (e.g., `shadow-sm` on the bottom navigation) to anchor fixed elements against the scrolling content.

## Shapes

The design uses a **Rounded** shape language to soften the industrial nature of the content.

- **Search Bars & Category Pills:** Use a full-radius (pill-shaped) design to encourage interaction.
- **Content Cards:** Use a standard 12px (rounded-xl) or 16px corner radius to provide a modern, friendly container for data.
- **Buttons:** Small tags and badges use a 6px-8px radius to maintain a compact profile.

## Components

- **Buttons:** Primary buttons are pill-shaped with high contrast. Text-only buttons (links) use the bold `label-md` style in the Primary color.
- **Search Bar:** A high-visibility component with a full-pill radius, using `surface-container-high` as the background and a `surface-variant` focus state.
- **Cards:** Vertical stacks with a subtle border (`outline-variant` at 20% opacity) or tonal background. They feature a clear header, body, and a bottom-aligned action area.
- **Chips/Badges:** Small, rounded containers for status (e.g., price change percentages). Use high-contrast semantic background colors (Red/Green) and small `label` typography.
- **Bottom Navigation:** Uses an "active pill" indicator for the current state, where the active icon is wrapped in a `secondary-container` background.
- **Input Fields:** Ghost-style inputs within containers, removing borders in favor of background-based boundaries.