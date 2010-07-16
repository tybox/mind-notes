package mindnotes.client.ui;

public interface DialogCallback<DialogResult> {
	public void dialogSuccessful(DialogResult dr);
	public void dialogCancelled();
}
