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

1. Copy the file to the folder you want TutorLink to keep its data in. This is TutorLink's _home folder_.

1. Open a terminal, `cd` to that folder, and run `java -jar tutorlink.jar`.<br>
   A window similar to the one below appears in a few seconds. The first time you start TutorLink, it contains some sample students.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box at the bottom and press Enter. Some commands to try:

   * `student add n/John Tan s/Math s/Physics` : Adds a student named John Tan who is taught Math and Physics.

   * `student view n/John Tan` : Shows John Tan's details.

   * `list` : Shows all your students.

   * `student delete n/John Tan` : Deletes John Tan.

   * `exit` : Exits the app.

1. See [Features](#features) below for the details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are parameters for you to fill in.<br>
  For example, in `student add n/NAME`, replace `NAME` with a name such as `John Tan`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [s/SUBJECT]` can be used as `n/John Tan s/Math` or as `n/John Tan`.

* Items followed by `…` can appear any number of times, including zero.<br>
  For example, `[s/SUBJECT]…` can be left out, or written as `s/Math` or `s/Math s/Physics`.

* Parameters can be in any order.<br>
  For example, `s/Math n/John Tan` works the same as `n/John Tan s/Math`.

* Command words are not case-sensitive. `Student Add` works the same as `student add`.

* Commands that take no parameters (`help`, `list` and `exit`) ignore anything typed after them.<br>
  For example, `help 123` is read as `help`.

* Every command that takes `n/NAME` finds the student by name, ignoring upper and lower case. `n/john tan` finds the student added as `John Tan`.

* If you are using a PDF version of this document, take care when copying commands that span several lines, because the spaces around line breaks may be lost.
</div>

### Viewing help: `help`

Opens a window with a link to this user guide.

Format: `help`

### Adding a student: `student add`

Adds a student, with the subjects you teach them.

Format: `student add n/NAME [s/SUBJECT]…`

* `NAME` is 1 to 50 characters long and contains at least one letter. It may use letters (including accented letters such as `é`), spaces, hyphens, apostrophes and full stops, so names such as `Mary-Jane O'Brien` and `Tan Ah Kow Jr.` are accepted.
* Each student's name must be unique, ignoring case. If `John Tan` exists, adding `john tan` is rejected as a duplicate.
* `SUBJECT` is 1 to 30 characters long and may use letters, digits, spaces, hyphens, `+` and `&`, for example `H2 Math` or `Biology + Chemistry`.
* A student can have any number of subjects, including none. Giving the same subject twice, in any case, is rejected.
* Subjects are shown in the order you type them.

Examples:
* `student add n/John Tan s/Math s/Physics`
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

Example:
* `student view n/john tan` shows the student added as `John Tan`.

### Listing all students: `list`

Shows all your students in the student list on the left.

Format: `list`

* Use `list` to show everyone again after a [`find`](#finding-students-by-name-find).

### Finding students by name: `find`

Shows only the students whose names contain any of the given words.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search ignores case. `john` matches `John`.
* Only whole words match. `Jo` does not match `John`.
* Students matching at least one word are shown. `find Tan Lim` shows both `John Tan` and `Aliyah Lim`.
* The student list shows `Showing 2 of 46` while the results are filtered. Type `list` to show everyone again.

Examples:
* `find tan` shows `John Tan` and `Tan Wei Ling`.
* `find john aliyah` shows `John Tan` and `Aliyah Lim`.

<!-- Interaction commands (interaction add, interaction list): @Dancodes2 -->

<!-- Follow-up commands (followup add, followup list): @elhanannw -->

### Deleting a student: `student delete`

Deletes a student, together with all of their interactions and follow-ups.

Format: `student delete n/NAME`

* The name is matched ignoring case, as in `student view`.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Deleting a student cannot be undone, and it also deletes every interaction and follow-up recorded for them.
</div>

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
To correct a student's name or subjects, delete the student and add them again. This also deletes their interactions and follow-ups.
</div>

Example:
* `student delete n/John Tan`

### Exiting the program: `exit`

Exits TutorLink.

Format: `exit`

### Saving the data

TutorLink saves your data to your computer after every command that changes it. You do not need to save manually.

### Editing the data file

TutorLink keeps its data in the JSON file `[JAR file location]/data/tutorlink.json`. Advanced users can edit this file directly.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, TutorLink starts with no students the next time it runs. The invalid file stays on disk until you run a command that changes data, which overwrites it. Back up the file before editing it.<br>
Some edits can also make TutorLink behave unexpectedly, for example a value outside the accepted range. Edit the data file only if you are confident you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I move my data to another computer?<br>
**A**: Install TutorLink on the other computer and run it once. Then replace the `data/tutorlink.json` file it creates with the one from your old computer's home folder.

**Q**: How do I remove the sample students?<br>
**A**: Delete each one with `student delete n/NAME`. TutorLink only adds sample students when it starts without a data file.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move TutorLink to a secondary screen and later switch to using only the primary screen, the window opens off-screen. To fix this, delete the `preferences.json` file in the home folder before running TutorLink again.
2. **If you minimize the help window** and then run `help` again (or use the `Help` menu, or press `F1`), the minimized help window stays minimized and no new one appears. Restore the minimized window manually.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add student** | `student add n/NAME [s/SUBJECT]…`<br> e.g., `student add n/John Tan s/Math s/Physics`
**View student** | `student view n/NAME`<br> e.g., `student view n/John Tan`
**List students** | `list`
**Find students** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find tan lim`
**Delete student** | `student delete n/NAME`<br> e.g., `student delete n/John Tan`
**Help** | `help`
**Exit** | `exit`
