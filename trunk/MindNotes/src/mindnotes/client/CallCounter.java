package mindnotes.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CallCounter {
	private static CallCounter __instance;
	private Map<String, Integer> _counts = new HashMap<String, Integer>();

	public static CallCounter get() {
		if (__instance == null)
			__instance = new CallCounter();
		return __instance;
	}

	public void reset() {
		_counts.clear();
	}

	public void register(String calltrace) {
		Integer i = _counts.get(calltrace);
		int x = i == null ? 1 : i + 1;
		_counts.put(calltrace, x);
	}

	public void print() {
		System.out.println("Call stats:");
		for (Entry<String, Integer> entry : _counts.entrySet()) {
			System.out.println("\t" + entry.getKey() + ": " + entry.getValue()
					+ " calls");
		}
		System.out.println("Done.");

	}
}
