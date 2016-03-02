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
* **Utilities** is a static class that provides various helper methods for simple sorting and test case generation.
* **Weighter** is an interface used to describe objects that can reinterpret distributions (e.g. of ratings) by filtering
data points or changing their effective frequency as per logical constraints (e.g. by removing all ratings submitted by students with less than 10% attendance).
* **GraphTools** is a collection of subclasses that implement the `Weighter` interface. It uses a Roster object to assign particular weights to `PeerInteraction`s based on intrinsic data (e.g. time of submission) or data from the `Student`s associated with said `PeerInteraction`s.

###### Passive Data Structures:
* **NetIDPair** is a simple staruct object that associates a netID string to an integer code. Once a `NetIDPair` is created, it does not directly allow access to the netID parameter for privacy reasons.
* **PeerInteraction** is a simple data structure that represents an entry from the CS125 lecture feedback database. It contains a pair of encoded NetIDs, a grade, two feedback strings, and a date string. Its primary constructor uses an `NRList` to take a CSV String and parse it as a `PeerInteraction`, with valid `PeerInteraction`s marked as such if the netIDs of the feedback passed in are distinct and both found in the `NRList` and all netID fields replaced with integer codes.
* **PeerInteractionsData** creates an array list of "uncleaned" feedback entries then creates a new "cleaned" list containing only valid `PeerInteraction` objects as well as printing stats on the number of valid entries.

###### Associative Containers:
* **NRList** is a simple map-like list of NetIDPairs with a file argument constructor. It supports the addition of new students through their netIDs, the removal of existing students through their netIDs, and will return a randomized (but consistent) code associated with each netID in the NRList given a particular netID. This class may be reworked to provide O(1) operations for lookup, insertion, etc. NetIDs inserted into NRList will be stored in `NetIDPairs`.
* **Roster** is a container which stores all `Student`s in the class for processing. It Implements a constant-time getter method which maps student ID codes to their corresponding `Student` objects. This class also has methods to add new `Student`s, change capacity, and also distribute collections of `PeerInteraction`s among `Student`s in the Roster. This class may need to be expanded to accomodate expansions of the `Student` class.

###### Research-Oriented Objects:
* **Lecture** is a class primarily associated with the time (a Java `Date` object) of a lecture. The `Lecture` object contains all `PeerInteraction`s associated with the particular lecture and an optional title, description, and number. `Lecture` provides a static binary search method that can associate a `PeerInteraction` with the right `Lecture` given a sorted List of `Lecture`s. It also computes several statistical variables such as rating mean and rating standard deviation. Lastly, it provides a distribution of all ratings in the `Lecture` subject to externally defined filters and weights (see `Weighter` and `GraphTools`).
* **LectureData** creates an array list of lectures by date and does basic analysis like finding the mean and standard deviation of the associated interactions.
* **Student** stores information regarding a particular student in class, including her lecture attendance and possibly grades. Each `Student` will contain all the proper `PeerInteraction`s made by the student. `Student` also provides basic statistical summarizes of its `PeerInteraction`s, in parallel to `Lecture`, and methods to merge together multiple `PeerInteraction`s submitted for the same `Lecture` (given a sorted list of `Lecture`s). More information will be stored in this class in the future.

###### Application and User Interface:
* **ProtoApp** is a prototypical combined application for the research project, designed to interweave all other classes to provide sufficient functionality for the purposes of various UI designs. This object contains an `NRList`, a `Roster`, and a List of `Lecture`s for a particular course. In addition, it has two strings for the course title and course description. More functionality will be added according to any shortfalls observed in the ongoing development of `SimpleUI`.
* **SimpleUI** is a fairly primitive command line application for testing the functionalities provided by all existing classes. It uses Unix-like commands from standard input to generate the data structures of this project and to eventually provide flexible visual and numerical representations of said data.

### Running:
* `Main`
	* Calls `PeerInteractionsData.initialize()`
		* Uses `PeerInteraction` constructors and state to create a raw and a cleaned list of entries.
	* Initializes lectures via the static method `LectureData.initialize()`
	 	* Uses `Lecture` constructors and state to create a list of lectures and do some basic analysis.

	
### Other:
* Travis CI current: https://travis-ci.org/Togira/CS125-Research
