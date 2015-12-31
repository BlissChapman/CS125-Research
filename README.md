# CS125-Research ![](https://travis-ci.org/Togira/CS125-Research.svg)
### Abstract
We provide a model, analysis and results from an experiment implementing a daily lecture feedback requirement in a large-lecture environment introductory computer science course. Gathering feedback early and often has been demonstrated to be effective at monitoring student progress and aids in identifying stumbling points in material and presentation throughout a course. We present results from an experiment taking this idea to an extreme. Daily feedback on our lectures (provided by **600 students** per semester) lets us keep a finger on the pulse of the course, immediately detecting topics that are unsettling to students and permitting rapid movement through material in which students express mastery.

> Our lecture feedback app is conveniently available on the web and in iOS and Android app formats.

Daily lecture feedback takes just seconds for students to submit with these tools. Each day, from these feedback apps, we gather **student ID and a partner ID, a lecture productivity rating (0-10) and a list of topics understood and any needing additional attention from each student**. Students are motivated to provide feedback because providing it: 

1. contributes a small amount toward their course grade
2. allows them to alter their learning environment by sitting in different locations and working on lecture activities with a different student each lecture session
3. means they will work with a variety of students of varying skill levels - experiencing both mentor and apprentice roles throughout the duration of the course.
	
While participating in the lecture feedback process has obvious benefits for the students and the course it also provides us with a rich dataset we use to mine for answers to important questions related to large-lecture course outcomes. We cross-reference the contributed lecture productivity ratings with student roster and performance information such as the **student’s major, college, year-in-school, gender, grades on weekly quizzes, and cumulative semester grades**.

From this data we attempt to identify relationships and provide answers to questions such as:

* Do students perform better on weekly quizzes when they work the prior week with students at higher, lower or about the same skill level as themselves?
* Do students claim their lecture sessions are more productive when they work with students at a higher, lower or about the same skill level?
* Do students of one gender report they have more productive lecture sessions with someone of the same or opposite gender?
* Do students who work with a large variety of student partners from a larger set of skill levels perform better or worse in the course overall than peers who primarily work with those of similar skill levels?
* Does performance on subsequent quizzes validate these opinions, or oppose them?
* **Optimal Pairing** - Is there such an algorithm that can use the data we have as inputs and pair students together every lecture such that the average performance of the class is as high as possible at the end of the semester? _If yes, can we find it?_

We provide a summary of these and other research questions in this work.


# Code Base Structure
The code base's structure is based on the raw data extracted from the lecture feedback database and the majority of the code so far is focused on manipulating this data into an easily analyzable state.  As you move down the list of classes described below, we move farther and farther away from the raw data, building off previous classes' abstractions.

## Summary by class:
###### Utilities:
* **TextIO** is a utility class used extensively throughout CS125 to make reading from a text file much simpler.

* **NetIDPair** is an ultra simple model of a Java object that holds the encrypted and non-encrypted information of a single net id from the roster.
* **NRList** is a simple map-like list of `NetIDPair` s that can be passed a file path (to the roster).

###### Peer Interaction Data:
* **PeerInteraction** is a simple data structure that represents an entry from the CS125 lecture feedback database. It contains a pair of encoded NetIDs, a grade, two feedback strings, and a date string.
* **PeerInteractionsData** creates an array list of "uncleaned" feedback entries then creates a new "cleaned" list containing only valid `PeerInteraction` objects as well as printing stats on the number of valid entries.
* **Lecture** is a model for a lecture object with a unique id corresponding to the chronological lecture number, all the associated feedback entries, the date, and the lecture topics.  Static methods within the lecture class run through all the `PeerInteraction` objects that exist, creates `Lecture` objects corresponding to the data we have and associates all of the `PeerInteraction` objects linked to that `Lecture` (The `Lecture` object model contains a list of Entries).
* **LectureData** creates an array list of lectures by date and does basic analysis like finding the mean and standard deviation of the associated interactions.

###### Other:
* **Student** stores information regarding a particular `Student` including their lecture attendance, possibly grades and gender, and all their feedback interactions. A list of `Student` objects may frequently be iterated over.



### Running:
* `Main`
	* Calls `PeerInteractionsData.initialize()`
		* Uses `PeerInteraction` constructors and state to create a raw and a cleaned list of entries.
	* Initializes lectures via the static method `LectureData.initialize()`
	 	* Uses `Lecture` constructors and state to create a list of lectures and do some basic analysis.

	
### Other:
* Travis CI current: https://travis-ci.org/Togira/CS125-Research
