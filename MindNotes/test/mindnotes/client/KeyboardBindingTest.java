package mindnotes.client;

import junit.framework.Assert;
import mindnotes.client.presentation.KeyboardShortcuts;
import mindnotes.client.presentation.KeyboardShortcuts.KeyBinding;

import org.junit.Test;

import com.google.gwt.user.client.Command;

public class KeyboardBindingTest {

	class Flag {
		boolean state;

		void up() {
			state = true;
		}

		void down() {
			state = false;
		}

		boolean isUp() {
			return state;
		}

		boolean isDown() {
			return !state;
		}

	}

	@Test
	public void testKeyboardBindings() {
		KeyboardShortcuts ks = new KeyboardShortcuts();
		final Flag flag = new Flag();
		ks.addBinding(new KeyBinding('Z', true, false, false, new Command() {

			@Override
			public void execute() {
				flag.up();
			}
		}));

		flag.down();
		ks.onShortcutPressed('s', false, false, false);
		Assert.assertTrue(flag.isDown());

		flag.down();
		ks.onShortcutPressed('z', true, false, false);
		Assert.assertTrue(flag.isUp());

		flag.down();
		ks.onShortcutPressed('z', true, true, false);
		Assert.assertTrue(flag.isDown());

		flag.down();
		ks.onShortcutPressed('Z', true, false, false);
		Assert.assertTrue(flag.isUp());

	}

	@Test
	public void testDoubleKeyboardBindings() {
		KeyboardShortcuts ks = new KeyboardShortcuts();
		final Flag flag1 = new Flag();
		final Flag flag2 = new Flag();
		ks.addBinding(new KeyBinding('Z', true, false, false, new Command() {

			@Override
			public void execute() {
				flag1.up();
			}
		}));

		ks.addBinding(new KeyBinding('Z', true, true, false, new Command() {

			@Override
			public void execute() {
				flag2.up();
			}
		}));

		flag1.down();
		flag2.down();
		ks.onShortcutPressed('z', true, false, false);
		Assert.assertTrue(flag1.isUp());
		Assert.assertTrue(flag2.isDown());

		flag1.down();
		flag2.down();
		ks.onShortcutPressed('z', true, true, false);
		Assert.assertTrue(flag1.isDown());
		Assert.assertTrue(flag2.isUp());

	}
}
