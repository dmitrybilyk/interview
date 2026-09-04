None-normalized db is the state where we even save several courses which student attends
in single cell (column) separated by comma. All entities maybe be in single table even.
A lot of nulls and a lot of needing to go via all table even when just single student's data
is changed.

### Fist normal form:
`Atomicity`: Each cell must contain only one value (no lists or comma-separated strings).

`No Repeating Groups`: You shouldn't have columns like course_1, course_2, course_3.

`Unique Rows`: Each record must be identifiable (usually by a Primary Key).

### Second normal form:
The table must be in 1NF AND every non-key column must depend on the entire primary key.

### Third normal flow:
3NF requires that no non-key column depends on another non-key column
