package mindnotes.client;

import java.util.ArrayList;
import java.util.List;

import mindnotes.client.ui.TemporaryInsertList;

import org.junit.Assert;
import org.junit.Test;

public class TemporaryInsertListTest {

	@Test
	public void testTIList() {
		List<String> list = new ArrayList<String>();

		TemporaryInsertList<String> til = new TemporaryInsertList<String>(list);

		list.add("foo");
		list.add("bar");
		list.add("baz");

		Assert.assertEquals(3, til.size());

		til.setTemporaryInsert(0, "ins");
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(4, til.size());
		int i = 0;
		for (String str : til) {
			i++;
			Assert.assertNotNull(str);
		}
		Assert.assertEquals(4, i);

		Assert.assertEquals("ins", til.get(0));
		Assert.assertEquals("foo", til.get(1));
		Assert.assertEquals("bar", til.get(2));
		Assert.assertEquals("baz", til.get(3));

		til.clearInsert();
		Assert.assertEquals(3, til.size());

		til.setTemporaryInsert(3, "ins");

		Assert.assertEquals("foo", til.get(0));
		Assert.assertEquals("bar", til.get(1));
		Assert.assertEquals("baz", til.get(2));
		Assert.assertEquals("ins", til.get(3));

		til.setTemporaryInsert(1, "ins");

		Assert.assertEquals("foo", til.get(0));
		Assert.assertEquals("ins", til.get(1));
		Assert.assertEquals("bar", til.get(2));
		Assert.assertEquals("baz", til.get(3));

	}

	@Test
	public void testListIterator1() {
		List<String> list = new ArrayList<String>();

		TemporaryInsertList<String> til = new TemporaryInsertList<String>(list);

		list.add("foo");
		list.add("bar");
		list.add("baz");

		til.setTemporaryInsert(3, "ins");

		int i = 0;
		for (String item : til) {

			switch (i++) {
			case 0:
				Assert.assertEquals("foo", item);
				break;
			case 1:
				Assert.assertEquals("bar", item);
				break;
			case 2:
				Assert.assertEquals("baz", item);
				break;
			case 3:
				Assert.assertEquals("ins", item);
				break;
			default:
				throw new RuntimeException();
			}
		}
	}

	@Test
	public void testIllegalIndex() {
		List<String> list = new ArrayList<String>();

		TemporaryInsertList<String> til = new TemporaryInsertList<String>(list);

		list.add("foo");
		list.add("bar");
		list.add("baz");

		til.setTemporaryInsert(4, "ins");

		Assert.assertEquals("ins", til.get(3));
	}
}
