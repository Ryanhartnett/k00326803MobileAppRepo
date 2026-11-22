# TusBookings – TUS Equipment Loans (K00326803)

Kotlin + Jetpack Compose app for borrowing college equipment.

- **Firebase Auth** sign in / sign up; **Firestore** `users/{uid}.role` decides admin.
- **Room** stores item types, items and loans (works offline, survives restarts).
- Items have several **units**; a loan takes 1–3 units with a **return-by date** and reason.
- **My loans** shows items out now and returned history; returning gives units back.
- **Admin** (role = "admin" set in the Firebase console) can add and delete items.
- Search, type filter and "Available now" filter on Browse; dark mode saved on device.

## Test plan
1. Sign up a new account → Home opens.
2. Sign in with a wrong password → error shown.
3. Borrow 2 units of a laptop → "Loan confirmed", availability drops by 2.
4. Submit with a blank return date / reason → field errors shown.
5. Return a loan → moves to "Returned", units go back up.
6. Close and reopen / rotate → loans and form text kept.
7. Admin adds an item → appears in Browse.
8. Two accounts → each only sees their own loans.
