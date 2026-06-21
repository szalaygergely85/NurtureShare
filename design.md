---
name: Nurture Share
colors:
  surface: '#fbf9f8'
  surface-dim: '#dbd9d9'
  surface-bright: '#fbf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f3'
  surface-container: '#efeded'
  surface-container-high: '#eae8e7'
  surface-container-highest: '#e4e2e2'
  on-surface: '#1b1c1c'
  on-surface-variant: '#424842'
  inverse-surface: '#303030'
  inverse-on-surface: '#f2f0f0'
  outline: '#737972'
  outline-variant: '#c2c8c0'
  surface-tint: '#4a654f'
  primary: '#4a654f'
  on-primary: '#ffffff'
  primary-container: '#8daa91'
  on-primary-container: '#253f2b'
  inverse-primary: '#b0ceb4'
  secondary: '#914b35'
  on-secondary: '#ffffff'
  secondary-container: '#ffa589'
  on-secondary-container: '#793823'
  tertiary: '#605e5c'
  on-tertiary: '#ffffff'
  tertiary-container: '#a6a2a0'
  on-tertiary-container: '#3a3937'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#cceacf'
  primary-fixed-dim: '#b0ceb4'
  on-primary-fixed: '#062010'
  on-primary-fixed-variant: '#334d38'
  secondary-fixed: '#ffdbd0'
  secondary-fixed-dim: '#ffb59e'
  on-secondary-fixed: '#3a0b00'
  on-secondary-fixed-variant: '#733420'
  tertiary-fixed: '#e6e2df'
  tertiary-fixed-dim: '#cac6c3'
  on-tertiary-fixed: '#1c1b1a'
  on-tertiary-fixed-variant: '#484644'
  background: '#fbf9f8'
  on-background: '#1b1c1c'
  surface-variant: '#e4e2e2'
typography:
  headline-lg:
    fontFamily: Quicksand
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Quicksand
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Quicksand
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.5px
  headline-lg-mobile:
    fontFamily: Quicksand
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  gutter: 16px
  margin-mobile: 16px
  margin-tablet: 24px
  card-padding: 20px
---

## Brand & Style
The design system is centered on the concept of "Shared Growth." It moves away from traditional clinical medical trackers and stereotypical nursery aesthetics, opting instead for a **Modern-Minimalist** approach with a heavy **Material Design 3 (MD3)** influence. 

The emotional goal is to provide a calm, grounded environment for couples. By utilizing soft, organic tones and generous whitespace, the UI reduces the cognitive load often associated with pregnancy and newborn care. The style is gender-neutral, focusing on the partnership of parenting rather than gender-specific tropes. It feels like a high-end lifestyle journal—warm, sophisticated, and dependable.

## Colors
The palette is derived from natural earth tones to evoke a sense of health and stability.

*   **Primary (Sage Green):** Used for growth-related actions, progress indicators, and primary navigation states. It represents health and vitality.
*   **Secondary (Terracotta):** Used for "Shared" moments, milestones, and emotional highlights. This warm tone draws the eye to collaborative features.
*   **Surface (Cream):** The foundation of the UI. This off-white background reduces eye strain compared to pure white and enhances the "lifestyle" feel.
*   **On-Surface (Slate Grey):** Used for all primary text and iconography to ensure high legibility while appearing softer than pure black.

## Typography
The system employs a dual-font strategy. **Quicksand** is used for headlines to provide a friendly, rounded character that feels approachable. For body copy and functional UI labels, **Plus Jakarta Sans** provides a modern, clean geometric structure that ensures high readability for data-heavy tracking logs.

All headings use a tighter letter-spacing to feel more cohesive, while labels use slightly expanded spacing for clarity at small sizes.

## Layout & Spacing
Following MD3 principles, the design system utilizes an **8dp grid** for all spacing and alignment. 

The layout is a **fluid grid** for mobile, adapting to a max-width container on larger devices. Content is organized into clear vertical stacks. On mobile devices, a 16px side margin is mandatory. Elements like cards and input fields should span the full width of the margins or be organized into two-column grids for smaller data points (e.g., "Last Feed" and "Diaper Change").

## Elevation & Depth
This design system avoids heavy shadows, favoring **tonal layers** and subtle elevation shifts. 

*   **Level 0 (Background):** The Cream (#FDF8F5) base.
*   **Level 1 (Cards/Surfaces):** A pure white (#FFFFFF) surface with a very soft, diffused shadow (4px blur, 4% opacity Slate Grey).
*   **Active States:** Elements that are being interacted with use a 2px stroke of the Primary Sage color rather than an increase in shadow.
*   **Overlays:** Modals and bottom sheets use a semi-transparent Slate Grey backdrop (20% opacity) to maintain focus without feeling heavy.

## Shapes
Shapes are intentionally soft to mirror the organic nature of the product's subject matter. 

Standard components (buttons, input fields) use a 0.5rem (8px) radius. Larger containers, such as feature cards and bottom sheets, utilize "rounded-xl" (1.5rem / 24px) for a modern, friendly appearance. Progress bars must be fully pill-shaped to avoid a clinical feel.

## Components

### Buttons & Inputs
*   **Primary Action:** Filled buttons in Sage Green with white text. 
*   **Shared Indicators:** Small, Terracotta-tinted chips with a "two-person" icon, used to indicate data synced from a partner.
*   **Text Fields:** Outlined style with a soft Slate Grey border that transitions to Sage Green on focus.

### Cards
*   Cards are the primary organizational unit. They should have a white background, no border, and the standard Level 1 elevation. 
*   Card headers should use Quicksand Medium with a small icon in the primary or secondary color.

### Progress Bars & Visuals
*   **Progress Indicators:** Used for pregnancy week tracking or daily goals. Use thick, rounded tracks (8px height) with Sage Green fills and a lighter Sage or Cream-tinted track background.
*   **Bottom Navigation:** A clean MD3-style bar with active states indicated by a subtle Sage Green pill shape behind the icon.

### Specialized Indicators
*   **Growth Markers:** Soft, circular indicators used in charts and logs.
*   **Partner Sync Status:** A small, unobtrusive pulse animation or icon in the top header to show real-time connection with a partner.
