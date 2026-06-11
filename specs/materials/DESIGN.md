---
name: Industrial Precision
colors:
  surface: '#f8f9fa'
  surface-dim: '#d9dadb'
  surface-bright: '#f8f9fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f4f5'
  surface-container: '#edeeef'
  surface-container-high: '#e7e8e9'
  surface-container-highest: '#e1e3e4'
  on-surface: '#191c1d'
  on-surface-variant: '#594238'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#f0f1f2'
  outline: '#8c7166'
  outline-variant: '#e0c0b2'
  surface-tint: '#a23f00'
  primary: '#9e3d00'
  on-primary: '#ffffff'
  primary-container: '#c64f00'
  on-primary-container: '#fffbff'
  inverse-primary: '#ffb595'
  secondary: '#4e6073'
  on-secondary: '#ffffff'
  secondary-container: '#cfe2f9'
  on-secondary-container: '#526478'
  tertiary: '#3a5aa0'
  on-tertiary: '#ffffff'
  tertiary-container: '#5473ba'
  on-tertiary-container: '#fefcff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdbcd'
  primary-fixed-dim: '#ffb595'
  on-primary-fixed: '#351000'
  on-primary-fixed-variant: '#7c2e00'
  secondary-fixed: '#d1e4fb'
  secondary-fixed-dim: '#b5c8df'
  on-secondary-fixed: '#091d2e'
  on-secondary-fixed-variant: '#36485b'
  tertiary-fixed: '#d9e2ff'
  tertiary-fixed-dim: '#b0c6ff'
  on-tertiary-fixed: '#001945'
  on-tertiary-fixed-variant: '#214489'
  background: '#f8f9fa'
  on-background: '#191c1d'
  surface-variant: '#e1e3e4'
typography:
  headline-lg:
    fontFamily: Manrope
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Manrope
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
  headline-sm:
    fontFamily: Manrope
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 24px
  body-lg:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-bold:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
  label-sm:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-page: 1.25rem
  gutter-grid: 1rem
  card-padding: 1.5rem
  stack-sm: 0.5rem
  stack-md: 1rem
---

## Brand & Style

This design system is engineered for the industrial sector, prioritizing high-legibility, structural clarity, and efficient navigation. The brand personality is utilitarian and dependable, evoking the feeling of a professional-grade tool. 

The aesthetic blends **Minimalism** with **Modern Corporate** influences. It utilizes heavy whitespace to reduce cognitive load during catalog browsing, while employing high-contrast typography to ensure data points are immediately scannable. The visual language is "built to last"—avoiding trendy fluff in favor of a robust, card-based architecture that feels organized and authoritative.

## Colors

The palette is rooted in industrial safety and architectural materials.

*   **Primary (Safety Orange):** Reserved strictly for interactive accents, such as the active state in bottom navigation and primary action buttons. This provides a clear visual signal within a neutral environment.
*   **Secondary (Iron Gray):** Used for primary text and structural icons to ensure high readability against the white background.
*   **Tertiary (Industrial Blue):** Employed for specific data highlights, such as pricing or status indicators, as seen in the product cards.
*   **Surface Colors:** A pure white background (`#FFFFFF`) is used for the main canvas, with light gray (`#F8F9FA`) used for secondary backgrounds and search input fields to create subtle separation.

## Typography

The design system uses **Manrope** for its modern, geometric construction and exceptional legibility at small sizes. 

Headlines utilize a heavy weight (`700` or `800`) to anchor the page and create a clear hierarchy. Body text is kept clean with standard weights, while "label-bold" is utilized for technical metadata and category tags, often paired with uppercase styling to mimic industrial labeling. On mobile devices, `headline-lg` should be capped at `24px` to ensure titles do not wrap excessively.

## Layout & Spacing

The system follows a **Fluid Grid** model optimized for mobile-first consumption. 

A standard 12-column grid is used for desktop, but on mobile, the layout relies on a single-column stack with `20px` (1.25rem) side margins. The vertical rhythm is governed by an 8px base unit. Product listings use a card-based layout where each card spans the full width of the content area. White space is used generously between sections to ensure that technical product details do not feel cramped or overwhelming.

## Elevation & Depth

This design system avoids heavy shadows in favor of **Low-contrast outlines** and **Tonal layers**. 

*   **Cards:** Product cards use a subtle `1px` border in a light neutral tone (`#E9ECEF`) rather than deep shadows. This maintains an "industrial blueprint" feel.
*   **Inputs:** Search bars and text fields use a slight inset or a soft background fill to denote interactivity without breaking the flat aesthetic.
*   **Overlays:** When text is placed over images (e.g., category banners), a linear gradient overlay (bottom-to-top, black at 60% opacity to transparent) is required to ensure the white typography remains accessible.

## Shapes

The shape language balances industrial rigidity with modern software approachability. 

The standard corner radius is `0.5rem` (8px). However, for **Product Details Cards**, a high-degree of roundedness (`1.5rem` or 24px) is used to create a distinct, containerized look that separates specific data points from the rest of the UI. Bottom navigation active indicators use pill-shaped containers for maximum contrast against the square edges of the screen.

## Components

*   **Bottom Navigation:** Icons use a medium stroke weight. The active state is indicated by the Primary Orange color and a soft, light-orange background pill around the icon.
*   **Product Cards:** These must include a clear Title, Manufacturer subtitle, and a list of specifications paired with small icons. The price should be prominently displayed in the bottom-left using the Tertiary Blue.
*   **Search Bar:** A full-width input with a `rounded-md` corner radius, featuring a magnifying glass icon and a subtle border.
*   **Action Buttons:** Primary buttons should be full-width on mobile, using the Primary Orange with white bold text.
*   **Category Banners:** Full-bleed or margin-to-margin images with a darkened gradient overlay and centered or bottom-aligned `headline-md` typography in white.