# Code Conventions

## Naming Conventions

**Files:**
- React components use PascalCase (`HabitDay.tsx`, `Header.tsx`).
- Other files, including routes and libraries, tend to use camelCase or kebab-case (`app.routes.tsx`, `global.css`).

Examples:
- `SummaryTable.tsx`
- `generate-dates-from-year-beginning.ts`
- `app.routes.tsx`

**Functions/Methods:**
- React component functions use PascalCase (`App`, `SummaryTable`).
- Other functions use camelCase (`generateDatesFromYearBeginning`).

Examples:
- `export function App() { ... }`
- `export function generateDatesFromYearBeginning() { ... }`

**Variables:**
- Variables and constants use camelCase.

Examples:
- `const [fontsLoaded] = useFonts(...)`
- `const app = Fastify()`
- `const weekDays = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S']`

## Code Organization

**Import/Dependency Declaration:**
- Imports are generally grouped, with external libraries first, followed by internal components and utility files. There doesn't appear to be a strict alphabetical or automated ordering.

Example (from `mobile/App.tsx`):
```typescript
import { StatusBar } from 'react-native'
import {
  useFonts,
  Inter_400Regular,
  // ...
} from '@expo-google-fonts/inter'
import './src/lib/dayjs'
import { Loading } from './src/components/Loading'
import { Routes } from './src/routes'
```

**File Structure:**
- Components are located in a `components` directory.
- Screens/pages are in a `screens` directory.
- Utility functions are in a `utils` directory.
- Library initializations/configurations are in a `lib` directory.

## Type Safety/Documentation

**Approach:**
- The entire codebase (web, mobile, server) uses TypeScript for type safety.
- Type definitions for specific domains like navigation are stored in `src/@types/`.

Example (`mobile/src/components/ProgressBar.tsx`):
```typescript
interface ProgressBarProps {
  progress?: number
}

export function ProgressBar({ progress = 0 }: ProgressBarProps) {
  // ...
}
```

## Error Handling

**Pattern:**
- No consistent, application-wide error handling pattern is immediately visible from the sample files. Error handling appears to be managed on a case-by-case basis.

## Comments/Documentation

**Style:**
- The code is sparsely commented. The focus is on self-documenting code through clear naming of variables and functions.
