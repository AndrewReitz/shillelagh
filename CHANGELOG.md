# Change Log

## Version 0.5.0 (11-30-2014)

- Added ability to define column, id, and table names.
- Renamed `@Field` annotation to `@Column`.
- Removed `@OrmOnly` annotation and need for empty no arg constructors.

## Version 0.4.0 (11-15-2014)

- Added query builder functionality. Returned from `selectFrom`
- Removed namespacing on tables. All tables MUST have unique names.
- Create statements written out.
