import java.util.Date;

import junit.framework.TestCase;

/**
* @author CS125Research
*/
public class LectureDataTest extends TestCase {
	
	public void tearDown() {
	}
	public void setUp() throws Exception{
		super.setUp();
	}
	
	public void testLectureObject() {
		Date sampleDate = new Date();
		PeerInteraction sampleInteraction = new PeerInteraction("\"43702\",\"null\",\"6\",,,\"2015-09-18 09:38:13\"");
		
		Lecture lectureOne = new Lecture(sampleDate);
		Lecture lectureTwo = new Lecture(sampleDate, sampleInteraction);
		String[] topics = {"topic1", "", null, "topic 2"};
		Lecture lectureThree = new Lecture(sampleDate, topics);
		
		Lecture[] testLectures = {lectureOne, lectureTwo, lectureThree};
		for (Lecture lecture : testLectures) {
			assertNotNull(lecture);
			assertNotNull(lecture.recordsByTime);
			assertNotNull(lecture.getDate());
		}
		
		assertTrue(lectureTwo.recordsByTime.size() == 1);
		lectureTwo.add(sampleInteraction);
		lectureTwo.add(sampleInteraction);
		assertTrue(lectureTwo.recordsByTime.size() == 3);
		
		//test auto-incrementer
		assertTrue(lectureOne.getLectureNumber() == 0);
		assertTrue(lectureTwo.getLectureNumber() == 1);
		assertTrue(lectureThree.getLectureNumber() == 2);
	}
	
	public void testValidAnalysisResults() {
		PeerInteractionsData.initialize();
		LectureData.initialize();
		
		assertNotNull(LectureData.lectures);
		assertTrue(LectureData.lectures.size() > 0);
		assertTrue(LectureData.mean > 0 && LectureData.mean < 10);
		assertTrue(LectureData.stdDev > 0);
	}
}
