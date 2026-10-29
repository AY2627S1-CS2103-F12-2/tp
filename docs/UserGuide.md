---
layout: page
title: User Guide
---

TutorLink is a **desktop app for private tutors who teach several students through recurring one-to-one lessons**. It keeps each student's subjects, dated notes on what happened in each lesson, and the things you want to revisit, all in one place. You use it by typing commands, so a tutor who types fast can find a student's context before a lesson in a few seconds.

TutorLink is for one tutor on one computer. It does not schedule lessons, track fees, keep grades or send messages.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `tutorlink.jar` from the [releases page](https://github.com/AY2627S1-CS2103-F12-2/tp/releases).

1. Copy the file to the folder you want TutorLink to keep its data in. This is TutorLink's _home folder_. Choose a folder you can write to, not a write-protected folder such as a system folder, because TutorLink saves its data there.

1. Open a terminal, `cd` to that folder, and run `java -jar tutorlink.jar`. Double-clicking the JAR file may not work on some computers, so use this command.<br>
   Open one TutorLink window per home folder. If the folder is already in use, a second launch shows
   `TutorLink is already using this data folder. Close the other TutorLink window and try again.`
   Close that dialog and continue in the existing window, or close the existing window before launching again.<br>
   A window similar to the one below appears in a few seconds. The first time you start TutorLink, it contains some sample students.<br>
   ![Ui](images/Ui.png)

   The window has five main areas:

   * The **student index** on the left shows all students in the current list.
   * The **command result** at the top confirms the latest command or explains what needs correcting.
   * The **student card** shows the selected student's name and subjects.
   * The **interactions** and **outstanding follow-ups** panels show the selected student's lesson history and
     work that still needs attention.
   * The **command box** at the bottom is where you type commands.

1. Try this short workflow. Type each command in the command box and press Enter:

   * `student add n/John Tan sub/Math sub/Physics` : Adds a student named John Tan who is taught Math and Physics.

   * `interaction add n/John Tan date/2026-10-01 note/Reviewed algebraic fractions` : Records what happened during a lesson.

   * `followup add n/John Tan desc/Check factorising homework review/2026-11-01` : Adds something to revisit with John.

   * `student view n/John Tan` : Selects John and shows his details, interaction history and outstanding follow-ups.

   * `followup done n/John Tan i/1` : Marks the first outstanding follow-up as completed.

   * `exit` : Exits the app.

   TutorLink saves each successful change automatically. There is no separate save command.

1. See [Features](#features) below for every command and its accepted input.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are parameters for you to fill in.<br>
  For example, in `student add n/NAME`, replace `NAME` with a name such as `John Tan`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [sub/SUBJECT]` can be used as `n/John Tan sub/Math` or as `n/John Tan`.

* Items followed by `…` can appear any number of times, including zero.<br>
  For example, `[sub/SUBJECT]…` can be left out, or written as `sub/Math` or `sub/Math sub/Physics`.

* Parameters can be in any order unless a command's instructions say otherwise.<br>
  For example, `sub/Math n/John Tan` works the same as `n/John Tan sub/Math`.
  For `interaction add`, `note/` must be last because everything after it is note text.

* Command words are not case-sensitive. `Student Add` works the same as `student add`.

* Commands that take no parameters (`help`, `list` and `exit`) ignore anything typed after them.<br>
  For example, `help 123` is read as `help`.

* Every command that takes `n/NAME` finds the student by name, ignoring upper and lower case. `n/john tan` finds the student added as `John Tan`.
  The same Unicode case matching is used for `find`, student lookup and duplicate checks.
  For example, `Işık` and `ışık` match the same student. Accents remain significant:
  `José` and `Jose` are different names.

* If you are using a PDF version of this document, take care when copying commands that span several lines, because the spaces around line breaks may be lost.
</div>

### Viewing help: `help`

Opens a window with a link to this user guide.

Format: `help`

### Adding a student: `student add`

Adds a student, with the subjects you teach them.

Format: `student add n/NAME [sub/SUBJECT]…`

* `NAME` is 1 to 50 characters long and contains at least one letter. It may use letters (including accented letters such as `é`), spaces, hyphens, apostrophes and full stops, so names such as `Mary-Jane O'Brien` and `Tan Ah Kow Jr.` are accepted.
  The limit counts Unicode code points after equivalent accented spellings are normalized.
  A supplementary CJK letter such as `𠀀` counts as one character. An `e` followed by a combining acute
  accent becomes `é` before counting and also counts as one character.
* `NAME` may also contain `s/o` or `d/o` as a separate word, in any case, as in `Raj s/o Kumar` or `Siti D/O Ahmad`.
* Each student's name must be unique, ignoring case. If `John Tan` exists, adding `john tan` is rejected as a duplicate.
* `SUBJECT` is 1 to 30 characters long and may use letters, digits, spaces, hyphens, `+` and `&`, for example `H2 Math` or `Biology + Chemistry`.
* Subject names support accented letters and combining marks, such as `Français` and `தமிழ்`.
  Equivalent accent spellings are treated as the same subject.
* The subject length limit counts Unicode code points after equivalent accented spellings are normalized.
  For example, `é` and `e` followed by a combining acute accent both count as one.
* A student can have any number of subjects, including none. Giving the same subject twice, in any case, is rejected.
* Subjects are shown in the order you type them.
* The new student is selected. If a [`find`](#finding-students-by-name-find) is active, the student list shows all your students again so the new student is in it.

Examples:
* `student add n/John Tan sub/Math sub/Physics`
* `student add n/Aliyah Lim`

On success, TutorLink shows the student and the new total:

```
✔ Student added: John Tan (subjects: Math, Physics)
Total students: 5
```

### Viewing a student: `student view`

Shows a student's name and subjects, and shows their interactions and follow-ups in the panels below.

Format: `student view n/NAME`

* The name is matched ignoring case, but it must otherwise be the full name. Use [`find`](#finding-students-by-name-find) if you only remember part of it.
* If a [`find`](#finding-students-by-name-find) is hiding the student, the student list shows all your students again so the student you view is in it.

Example:
* `student view n/john tan` shows the student added as `John Tan`.

### Editing a student: `student edit`

Renames a student, replaces their subjects, or both. Their interactions and follow-ups are kept.

Format: `student edit n/NAME [new/NEW_NAME] [sub/SUBJECT]…`

* `NAME` is the student's current full name, ignoring case.
* Give at least one of `new/` or `sub/`.
* `NEW_NAME` follows the same rules as in [`student add`](#adding-a-student-student-add). Renaming to the name of another existing student, ignoring case, is rejected as a duplicate. Changing only the casing, such as `john tan` to `John Tan`, is allowed.
* If you give any `sub/`, the subjects you give replace **all** the student's existing subjects. Without `sub/`, the subjects stay the same.
* If the new name and subjects are exactly the same as the current ones, nothing changes and TutorLink shows `No changes made: John Tan already has this name and these subjects.`
* If the student is shown in the panels, the panels update straight away. If you used `find` and the new name no longer matches it, the student leaves the filtered list and the panels are cleared. Use `list` to see all students again.

Examples:
* `student edit n/John Tan new/Jonathan Tan` renames John Tan.
* `student edit n/John Tan sub/Math sub/Chemistry` changes John Tan's subjects to Math and Chemistry.

On success, TutorLink shows the updated student:

```
✔ Student updated: Jonathan Tan (subjects: Math, Physics)
```

If the student has no subjects left, the message ends with `(no subjects recorded)`, as in `student add`.

### Listing all students: `list`

Shows all your students in the student list on the left.

Format: `list`

* Use `list` to show everyone again after a [`find`](#finding-students-by-name-find).

### Finding students by name: `find`

Shows only the students whose names contain any of the given words.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search ignores case. `john` matches `John`.
* Only whole words match. `Jo` does not match `John`.
* Parts of a name split by a hyphen, apostrophe or full stop count as words too. `connor` and `jones` both match `O'Connor-Jones`.
* Students matching at least one word are shown. `find Tan Lim` shows both `John Tan` and `Aliyah Lim`.
* The student list shows `Showing 2 of 46` while the results are filtered. Type `list` to show everyone again.

Examples:
* `find tan` shows `John Tan` and `Tan Wei Ling`.
* `find john aliyah` shows `John Tan` and `Aliyah Lim`.

<!-- Interaction commands (interaction add, interaction list): @Dancodes2 -->

### Adding an interaction: `interaction add`

Records a dated note about a lesson or other discussion with an existing student.

Format: `interaction add n/NAME date/DATE [t/TIME] note/TEXT`

* `NAME` identifies an existing student by their full name, ignoring case.
* `DATE` must be a real calendar date in `yyyy-MM-dd` format, such as `2026-10-01`.
  A future date is allowed, but TutorLink warns you before recording it.
* `TIME` is optional. If given, it must be a valid 24-hour time from `00:00` to `23:59`,
  inclusive, in `HH:mm` format, such as `14:30`. A `t/` with no value records the interaction without a time.
* `TEXT` is 1 to 500 characters long after leading and trailing whitespace is removed.
  It must not be blank and must be on a single line. If you paste text containing line breaks, each line break becomes a space,
  so a pasted `First line⏎Second line` is recorded as `First line Second line`.
  The limit counts Unicode code points: a simple emoji such as 😀 counts as one. A combined emoji or a letter with a combining accent can contain multiple code points and count as more than one.
* The `n/`, `date/` and optional `t/` parameters may appear in any order before `note/`.
  The `note/` parameter must be last. Everything after `note/` is treated as note text.
  Missing required parameters (`n/`, `date/`, or `note/`), repeated `n/`, `date/`, or `t/` parameters,
  and text before the first parameter are rejected.
* Multiple interactions may have the same date and time. Each note is recorded separately.
* A failed command leaves your stored data unchanged.

Examples:

* `interaction add n/John Tan date/2026-10-01 note/Discussed revision plan`
* `interaction add n/john tan date/2026-10-01 t/14:30 note/Practised algebraic fractions`
* `interaction add n/John Tan date/2026-10-15 note/Reviewed algebraic fractions`

After the first example, TutorLink shows:

```text
✔ Interaction recorded for John Tan on 2026-10-01.
Total interactions for John Tan: 1
```

If the interaction has a time, that time is included in the confirmation:

```text
✔ Interaction recorded for John Tan on 2026-10-01 (14:30).
Total interactions for John Tan: 2
```

If the date is in the future, TutorLink records the interaction and shows a warning first:

```text
⚠ Interaction date 2026-10-15 is in the future (today is 2026-10-14); recording it anyway.
✔ Interaction recorded for John Tan on 2026-10-15.
Total interactions for John Tan: 3
```

Invalid input examples:

* `note/` with no text is rejected with `Note cannot be empty.`
* A note longer than 500 characters is rejected with `Note must be 500 characters or fewer.`
* Repeated `n/`, `date/` or `t/` parameters before `note/` are rejected. A `note/` appearing
  after the first `note/` is retained as part of the note text.
* `date/01-10-2026` is rejected because the required format is `yyyy-MM-dd`.
* `date/2026-02-30` is rejected because February does not have 30 days.
* `t/24:00` and `t/25:00` are rejected because the hour must be from `00` to `23`.
* An unknown student is rejected with `No student named 'NAME' found.`

### Viewing a student's interactions: `interaction list`

Shows a student's interaction history in chronological order in the command result area.

Format: `interaction list n/NAME`

* Give the full name of an existing student, ignoring case, with `n/` exactly once.
* Interactions are sorted by date and then time, earliest first. On the same date, an interaction
  without a time and one at `00:00` share the same ordering position; their relative order follows
  the order in which they were recorded. Both appear before interactions at later times.
* Listing interactions does not change stored data or select a different student in the panels.
  Use `student view n/NAME` to select a student.

Example:

* `interaction list n/John Tan`

After recording the interactions above, the result is:

```text
✔ Interactions for John Tan (3):
1. 2026-10-01 — Discussed revision plan
2. 2026-10-01 14:30 — Practised algebraic fractions
3. 2026-10-15 — Reviewed algebraic fractions
```

If John has no interactions, TutorLink shows `ℹ No interactions recorded for John Tan yet.`
An unknown student gives `No student named 'NAME' found.` Missing or repeated `n/` parameters
are rejected.

### Editing an interaction: `interaction edit`

Corrects one or more fields of an interaction in an existing student's history.

Format: `interaction edit n/NAME i/INDEX [date/DATE] [t/TIME] [note/TEXT]`

* `INDEX` is the interaction number shown by `interaction list n/NAME` and must be a positive integer.
* At least one of `date/`, `t/` or `note/` must be supplied. Fields that are not supplied remain unchanged.
* Date, time, and note values follow the same rules as `interaction add`. A future replacement date is allowed
  with a warning. The `note/` parameter must be last, and everything after it is treated as note text.
* An existing time can be replaced using `t/TIME`, removed using `t/` with no value,
  or preserved by omitting `t/`.
* Changing the date or time can renumber the list. Run `interaction list` again before choosing another item.
* A failed command leaves your stored data unchanged.

Examples:

* `interaction edit n/John Tan i/2 note/Practised algebraic fractions and equations`
* `interaction edit n/John Tan i/2 date/2026-10-02 t/15:00`
* `interaction edit n/John Tan i/2 date/2026-10-02 t/ note/Reviewed algebra`

After the first example, TutorLink shows the original and updated interaction:

```text
✔ Interaction updated for John Tan: 2026-10-01 14:30 — Practised algebraic fractions
Updated to: 2026-10-01 14:30 — Practised algebraic fractions and equations
```

If item 2 is still the interaction shown above, the third example changes its date and note while
removing its existing time. TutorLink shows:

```text
✔ Interaction updated for John Tan: 2026-10-01 14:30 — Practised algebraic fractions and equations
Updated to: 2026-10-02 — Reviewed algebra
```

An unknown student, an out-of-range index, missing edit fields, invalid values, or repeated structured
parameters are rejected. A zero, negative, or non-integer index gives `Index must be a positive integer.`

### Deleting an interaction: `interaction delete`

Deletes one interaction from an existing student's history.

Format: `interaction delete n/NAME i/INDEX`

* `NAME` identifies an existing student by their full name, ignoring case.
* `INDEX` is the interaction number shown by `interaction list n/NAME`. It must be a positive integer.
* Because `interaction list` displays interactions chronologically, the index refers to that displayed order,
  not the order in which the interactions were recorded.
* The list is renumbered after deletion. Run `interaction list` again before choosing another item.
* An index larger than the number of interactions is rejected with a message giving the valid range, such as
  `Index 3 is out of range: John Tan's interactions are numbered 1 to 2.` If the student has no interactions,
  TutorLink shows `John Tan has no interactions.`
* A failed command leaves your stored data unchanged.

Example:

* `interaction delete n/John Tan i/2`

Using the interaction list above, TutorLink deletes the interaction dated `2026-10-01 14:30` and shows:

```text
✔ Interaction deleted for John Tan: 2026-10-01 14:30 — Practised algebraic fractions
Total interactions for John Tan: 2
```

An unknown student gives `No student named 'NAME' found.` Missing or repeated `n/` or `i/` parameters
are rejected. A zero, negative, or non-integer index is rejected with `Index must be a positive integer.`

### Adding a follow-up: `followup add`

Records something you intend to revisit with an existing student, with a review date.

Format: `followup add n/NAME desc/TEXT review/DATE`

* `NAME` identifies an existing student by their full name, ignoring case.
* `TEXT` is 1 to 200 characters long after leading and trailing whitespace is removed. It must not be blank and must be on a single line. If you paste text containing line breaks, each line break becomes a space.
  The limit counts Unicode code points: a simple emoji such as 😀 counts as one. A combined emoji or a letter with a combining accent can contain multiple code points and count as more than one.
* `DATE` must be a real calendar date in `yyyy-MM-dd` format, such as `2026-10-15`. Past, present and future review dates are allowed.
* Parameters can be in any order; `desc/` does not need to be last.
* Give each parameter exactly once. Missing parameters or repeated parameter prefixes are rejected.
* `TEXT` can contain literal text such as `review/worksheet` when you also give a separate `review/DATE`
  in `yyyy-MM-dd` format. Keep the literal text within the description, before any following parameter.
  For example, `followup add n/John Tan desc/Discuss review/worksheet review/2026-11-01` records
  `Discuss review/worksheet` as the description.
* After a space within `TEXT`, a `review/` value is literal text only if it is non-empty and its first character
  is not a digit. An empty or number-starting value is treated as a date parameter instead.
  For example, `review/2026-11-01` cannot be included literally in the description; supplying it alongside
  another review date is rejected as a repeated parameter.
* `n/` and `desc/` after a space always start parameters, so they cannot be included literally in `TEXT`.
* A follow-up belongs to the student. It does not need to be linked to an interaction.
* An outstanding duplicate with the same description and review date for the same student is rejected. Descriptions are compared after trimming and are case-sensitive: `Review algebra` and `review algebra` are different descriptions. You may add the same description and review date again after the earlier follow-up is completed or cancelled.
* A failed command leaves your stored data unchanged.

Examples (after adding John Tan):

* `followup add n/John Tan desc/Review algebra review/2026-10-15`
* `followup add n/john tan desc/Check in on extra drills review/2026-10-14`

After the first example, if John Tan has no other follow-ups, TutorLink shows:

```text
✔ Follow-up added for John Tan — review on 2026-10-15: "Review algebra"
Total outstanding follow-ups for John Tan: 1
```

Use `student view n/John Tan` to show John's follow-ups in the outstanding follow-ups panel. If John is already selected, the panel updates when you add a follow-up. Follow-ups are saved automatically and remain available after restarting TutorLink.

Invalid input examples:

* `desc/` with no text is rejected with `Description cannot be empty.`
* A description longer than 200 characters is rejected with `Description must be 200 characters or fewer.`
* `review/15-10-2026` is rejected because the required format is `yyyy-MM-dd`.
* `review/2026-02-30` is rejected because February does not have 30 days.
* `followup add n/John Tan desc/Discuss review/2026-10-01 review/2026-11-01` is rejected because it
  contains two review-date parameters.
* An unknown student is rejected with `No student named 'NAME' found.`

### Viewing outstanding follow-ups: `followup list`

Shows a student's outstanding follow-ups in the command result area.

Format: `followup list n/NAME`

* Give the full name of an existing student, ignoring case, with `n/` exactly once.
* Follow-ups are numbered and sorted by review date, earliest first. Follow-ups with the same review date remain in the order they were added. The outstanding follow-ups panel uses the same ordering.
* Only follow-ups with status `OUTSTANDING` are shown, including those with past review dates. Completed (`DONE`) and cancelled (`CANCELLED`) records remain saved but are excluded from this list and the panel. Passing a review date does not complete a follow-up automatically.
* Listing follow-ups does not change stored data or select a different student in the panels. Use `student view n/NAME` to select a student.

Example:

* `followup list n/John Tan`

After adding the two examples above, the result is:

```text
✔ Outstanding follow-ups for John Tan (2):
1. 2026-10-14 — Check in on extra drills
2. 2026-10-15 — Review algebra
```

If John has no follow-ups, TutorLink shows `ℹ No outstanding follow-ups for John Tan.` An unknown student gives `No student named 'NAME' found.` Missing or repeated `n/` parameters are rejected.

### Completing a follow-up: `followup done`

Marks an outstanding follow-up as completed, removing it from the outstanding list and panel while retaining it in the data file with status `DONE`.

Format: `followup done n/NAME i/INDEX`

* Give the full name of an existing student, ignoring case, and each parameter exactly once. Parameters may be in either order.
* `INDEX` must be a positive integer shown by `followup list n/NAME`. It refers to that student's outstanding follow-ups in review-date order, not their position in the data file.
* The list is renumbered after completion. Run `followup list` again before choosing another item.
* Completing a follow-up leaves the student's other follow-ups, interactions, name and subjects unchanged. If the student is selected, the panel updates automatically.
* The completed record and its status survive restarting TutorLink. Older v1.3 records without a status load as outstanding.

Example, continuing from the two follow-ups listed above:

```text
followup done n/John Tan i/1
```

TutorLink shows:

```text
✔ Follow-up completed for John Tan — review on 2026-10-14: "Check in on extra drills"
Total outstanding follow-ups for John Tan: 1
```

The algebra follow-up is now item 1. The completed drills follow-up remains saved, and you can add the same description and review date again if needed.

Errors leave all records unchanged:

* An unknown student gives `No student named 'NAME' found.`
* An empty index, zero, a negative number, a non-integer, or a value larger than `2147483647` gives `Index must be a positive integer.`
* An index larger than the current outstanding list gives the valid range, such as `Index 3 is out of range: John Tan's outstanding follow-ups are numbered 1 to 2.` If there are no outstanding follow-ups, TutorLink shows `John Tan has no outstanding follow-ups.`
* Missing parameters or extra text before them give a format error with the correct command format. Repeated `n/` or `i/` prefixes are rejected.

### Cancelling a follow-up: `followup cancel`

Marks an outstanding follow-up as cancelled when you no longer intend to revisit it. It is removed from the outstanding list and panel but remains saved with status `CANCELLED`.

Format: `followup cancel n/NAME i/INDEX`

* Give the full name of an existing student, ignoring case, and each parameter exactly once. Parameters may be in either order.
* `INDEX` must be a positive integer shown by `followup list n/NAME`. It refers to that student's outstanding follow-ups in review-date order, not their position in the data file.
* The list is renumbered after cancellation. Run `followup list` again before choosing another item. Completed and cancelled follow-ups cannot be selected from this list.
* Cancelling a follow-up leaves the student's other follow-ups, interactions, name and subjects unchanged. If the student is selected, the panel updates automatically.
* The cancelled record and its status survive restarting TutorLink. You may add the same description and review date again if needed.

Example, continuing after the completion example above, where the algebra follow-up is the only outstanding item:

```text
followup list n/John Tan
followup cancel n/John Tan i/1
```

After the cancellation command, TutorLink shows:

```text
✔ Follow-up cancelled for John Tan — review on 2026-10-15: "Review algebra"
Total outstanding follow-ups for John Tan: 0
```

The cancelled algebra follow-up and the completed drills follow-up remain saved. Running `followup list n/John Tan` now shows `ℹ No outstanding follow-ups for John Tan.`

Errors leave all records unchanged:

* An unknown student gives `No student named 'NAME' found.`
* An empty index, zero, a negative number, a non-integer, or a value larger than `2147483647` gives `Index must be a positive integer.`
* An index larger than the current outstanding list gives the valid range, such as `Index 3 is out of range: John Tan's outstanding follow-ups are numbered 1 to 2.` If there are no outstanding follow-ups, TutorLink shows `John Tan has no outstanding follow-ups.`
* Missing parameters or extra text before them give a format error with the correct command format. Repeated `n/` or `i/` prefixes are rejected.

### Deleting a student: `student delete`

Deletes a student, together with all of their interactions and follow-ups.

Format: `student delete n/NAME`

* The name is matched ignoring case, as in `student view`.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Deleting a student cannot be undone, and it also deletes every interaction and follow-up recorded for them.
</div>

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
To correct a student's name or subjects, use [`student edit`](#editing-a-student-student-edit) instead. It keeps their interactions and follow-ups.
</div>

Example:
* `student delete n/John Tan`

### Exiting the program: `exit`

Exits TutorLink.

Format: `exit`

### Saving the data

TutorLink saves your data to your computer after every command that changes it. You do not need to save manually.

Only one running copy of TutorLink can use a home folder at a time. Copies started from different home folders
keep separate data.

TutorLink creates `data/tutorlink.json.lock` to coordinate access to the data file. This file stores no student data
and can remain after a normal exit or a crash. Once the previous TutorLink process has stopped, the file does not
prevent the next launch. Leave it in place; do not delete or rename it while TutorLink is running.

### Editing the data file

TutorLink keeps its data in the JSON file `data/tutorlink.json` inside its home folder (the folder containing `tutorlink.jar`). Advanced users can edit this file directly.

Student names in the file must be unique under the same rules for ignoring case as commands.
An older file containing both `Işık` and `ışık` is invalid, so the recovery steps below apply.
To preserve both records, give one student a distinct name in a copy of the file before restoring it.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, TutorLink starts with no students the next time it runs and shows a warning. It saves a copy of the invalid file as `data/tutorlink.json.bak` (replacing any earlier copy), because your next command that changes data overwrites `data/tutorlink.json`. To recover, close TutorLink, fix the copy and rename it back to `tutorlink.json`. Back up the file before editing it.<br>
Some edits can also make TutorLink behave unexpectedly, for example a value outside the accepted range. Edit the data file only if you are confident you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I move my data to another computer?<br>
**A**: Close TutorLink on both computers. Copy the `data` folder, which contains `tutorlink.json`, from the folder with `tutorlink.jar` on your old computer into the folder with `tutorlink.jar` on the new computer, replacing any existing `data` folder. Then start TutorLink on the new computer.

**Q**: How do I remove the sample students?<br>
**A**: Delete each one with `student delete n/NAME`. TutorLink only adds sample students when it starts without a data file.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move TutorLink to a secondary screen and later switch to using only the primary screen, the window opens off-screen. To fix this, delete the `preferences.json` file in the home folder before running TutorLink again.
2. **If you minimize the help window** and then run `help` again (or use the `Help` menu, or press `F1`), the minimized help window stays minimized and no new one appears. Restore the minimized window manually.
3. **If TutorLink is in a write-protected folder**, it cannot start when its data lock cannot be created or opened.
   It shows `TutorLink could not open its data folder safely. Check that the folder is writable and try again.`
   If the app opens but lacks permission to save student data, commands that change data report insufficient
   permissions. Close TutorLink and move `tutorlink.jar` and any existing `data` folder to a writable home folder,
   then start it again.
4. **On macOS, if TutorLink is in full-screen mode**, the help window also opens in full-screen mode, in its own space. Closing it takes you to the desktop instead of back to TutorLink, which is still open in its own full-screen space. Switch back to TutorLink with `Cmd+Tab` or by swiping between spaces, or exit full-screen mode before using `help`.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add student** | `student add n/NAME [sub/SUBJECT]…`<br> e.g., `student add n/John Tan sub/Math sub/Physics`
**View student** | `student view n/NAME`<br> e.g., `student view n/John Tan`
**Edit student** | `student edit n/NAME [new/NEW_NAME] [sub/SUBJECT]…`<br> e.g., `student edit n/John Tan new/Jonathan Tan sub/Math`
**List students** | `list`
**Find students** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find tan lim`
**Add interaction** | `interaction add n/NAME date/DATE [t/TIME] note/TEXT`<br> e.g., `interaction add n/John Tan date/2026-10-01 note/Discussed revision plan`
**List interactions** | `interaction list n/NAME`<br> e.g., `interaction list n/John Tan`
**Edit interaction** | `interaction edit n/NAME i/INDEX [date/DATE] [t/TIME] [note/TEXT]`<br> e.g., `interaction edit n/John Tan i/2 note/Corrected lesson note`
**Delete interaction** | `interaction delete n/NAME i/INDEX`<br> e.g., `interaction delete n/John Tan i/2`
**Add follow-up** | `followup add n/NAME desc/TEXT review/DATE`<br> e.g., `followup add n/John Tan desc/Review algebra review/2026-10-15`
**View follow-ups** | `followup list n/NAME`<br> e.g., `followup list n/John Tan`
**Complete follow-up** | `followup done n/NAME i/INDEX`<br> e.g., `followup done n/John Tan i/1`
**Cancel follow-up** | `followup cancel n/NAME i/INDEX`<br> e.g., `followup cancel n/John Tan i/1`
**Delete student** | `student delete n/NAME`<br> e.g., `student delete n/John Tan`
**Help** | `help`
**Exit** | `exit`
