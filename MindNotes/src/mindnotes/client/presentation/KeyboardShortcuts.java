package mindnotes.client.presentation;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Command;

/**
 * The KeyboardShortcuts is responsible for translating received keystroke
 * messages into commands. For example, when ctrl+Z is pressed, this class will
 * look for a binding and will find the Undo binding added by MindMapPresenter.
 * 
 * In the future, it is possible that this class could load a config file
 * defining key binds for commands.
 * 
 * @author dominik
 * 
 */
public class KeyboardShortcuts {

	public static class KeyBinding {
		private int _key;
		private boolean _shift;
		private boolean _ctrl;
		private boolean _alt;
		private Command _command;

		public KeyBinding(int key, boolean ctrl, boolean shift, boolean alt,
				Command cmd) {

			_key = key;
			_ctrl = ctrl;
			_shift = shift;
			_alt = alt;
			_command = cmd;

		}

		public boolean matches(int key, boolean ctrl, boolean shift, boolean alt) {
			return key == _key && _ctrl == ctrl && _shift == shift
					&& _alt == alt;
		}

		public Command getCommand() {
			return _command;
		}

		public int getKey() {
			return _key;
		}

		@Override
		public int hashCode() {
			return bindingCode(_key, _ctrl, _shift, _alt);
		}

		public static int bindingCode(int key, boolean ctrl, boolean shift,
				boolean alt) {
			return key << 3 + (ctrl ? 1 : 0) + (shift ? 2 : 0) + (alt ? 4 : 0);
		}
	}

	private Map<Integer, KeyBinding> _bindings;

	public KeyboardShortcuts() {
		_bindings = new HashMap<Integer, KeyboardShortcuts.KeyBinding>();
	}

	public void addBinding(KeyBinding binding) {
		_bindings.put(binding.hashCode(), binding);
	}

	public void onShortcutPressed(int key, boolean ctrl, boolean shift,
			boolean alt) {
		if (Character.isLetter((char) key)) {
			key = Character.toUpperCase((char) key);
		}
		KeyBinding binding = _bindings.get(KeyBinding.bindingCode(key, ctrl,
				shift, alt));
		if (binding != null && binding.matches(key, ctrl, shift, alt)) {
			binding.getCommand().execute();
		}

	}

}
