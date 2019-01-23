package com.android.example.rewardify;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
Dialog box for editing archived tasks
*/
public class TasksArchivedDialogFragment extends DialogFragment {
	private String oldName;

	//Used to determine whether to archive the fragment or not
	private boolean restore;
	private boolean delete;

	private ArrayList<String> refreshTypes;

	private View rootView;

	private Spinner frequency_spinner;
	private EditText nameET;
	private EditText repeatsET;
	private	EditText expET;


	public TasksArchivedDialogFragment() {
		// Empty constructor is required for DialogFragment
		// Make sure not to add arguments to the constructor
		// Use `newInstance` instead as shown below
	}

	public static TasksArchivedDialogFragment newInstance(String name, int repeats, String interval, int exp) {
		TasksArchivedDialogFragment frag = new TasksArchivedDialogFragment();

		//Put arguments in bundle
		Bundle args = new Bundle();
		args.putString("name", name);
		args.putInt("repeats", repeats);
		args.putString("interval", interval);
		args.putInt("exp", exp);
		frag.setArguments(args);

		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_tasks_archived_dialog, container);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initiateUI();
		createListeners();
	}

	//Source: https://stackoverflow.com/questions/15121373/returning-string-from-dialog-fragment-back-to-activity
	public static interface OnArchivedCompleteListener {
		public abstract void onArchivedComplete(String oldName, String name, int repeats, String Interval, int exp, boolean archive, boolean delete);
	}

	private OnArchivedCompleteListener mListener;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			this.mListener = (OnArchivedCompleteListener)context;
		}
		catch (final ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implement OnActiveCompleteListener");
		}
	}

	//Initialises UI on startup
	public void initiateUI()
	{
		//get bundle Variables
		Bundle args = getArguments();
		String name = args.getString("name");
		int repeats = args.getInt("repeats");
		String Interval = args.getString("interval");
		int exp = args.getInt("exp");

		oldName = name;
		restore = false;
		delete = false;

		//Fill edit text views
		nameET = (EditText) rootView.findViewById(R.id.name_et);
		nameET.setText(name);
		repeatsET = (EditText) rootView.findViewById(R.id.repeats_et);
		repeatsET.setText(""+repeats);
		expET = (EditText) rootView.findViewById(R.id.exp_et);
		expET.setText(""+exp);

		//Initiate Spinner
		frequency_spinner = (Spinner) rootView.findViewById(R.id.frequency_spinner);
		refreshTypes = new ArrayList<String>(Arrays.asList("daily", "weekly"));

		//Populate Spinner using adaptor and second list
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, refreshTypes);
		frequency_spinner.setAdapter(adapter);

		//Set Spinner Selection
		frequency_spinner.setSelection(adapter.getPosition(Interval));
	}


	//Dismiss when cancel pressed
	public void cancelPressed(){
		this.dismiss();
	}
	
	//Restore task when restor button pressed
	public void restorePressed(){
		restore = true;
		okPressed();
	}

	public void deletePressed(){
		delete = true;
		okPressed();
	}

	//Process data when ok pressed
	public void okPressed(){

		//Get Values
		String name = nameET.getText().toString();
		String total = repeatsET.getText().toString();
		String xpPerPoint = expET.getText().toString();


		String frequency = frequency_spinner.getSelectedItem().toString();

		if (validInput(name, total, xpPerPoint))
		{
			this.mListener.onArchivedComplete(oldName, name, Integer.parseInt(total), frequency, Integer.parseInt(xpPerPoint), restore, delete);
			this.dismiss();
		}
	}

	/**
	 * Checks user input is valid
	 */
	private boolean validInput(String name, String repeats, String exp)
	{
		//Check if name field is blank
		if (name.equals("")) {
			//(MainActivity)getActivity().fileIO
			makeToast("Error: Name Field is blank.", false);
			return false;
		}
		//TODO: Check if name already exists
		else if (false) {
			makeToast("Error: Name Field is blank.", false);
			return false;
		}
		//check if repeats is 0
		else if(repeats.equals("") || (repeats.matches("[0]+"))) {
			makeToast("Error: repeats can't be zero.", false);
			return false;
		}
		//check if exp is 0
		else if (exp.equals("") || (exp.matches("[0]+"))) {
			makeToast("Error: experience can't be zero.", false);
			return false;
		}
		else{
			return true;
		}
	}

	//Creates button listeners on startup
	private void createListeners()
	{
		Button okBtn = (Button) rootView.findViewById(R.id.ok_btn);
		okBtn.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						okPressed();
					}
				});

		Button cancelButton = (Button) rootView.findViewById(R.id.cancel_btn);
		cancelButton.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						cancelPressed();
					}
				});
		Button archiveButton = (Button) rootView.findViewById(R.id.restore_button);
		archiveButton.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						restorePressed();
					}
				});
		Button deleteButton = (Button) rootView.findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						deletePressed();
					}
				});
	}

	/**
	 * Used to easily make a toast
	 **/
	private void makeToast(String message, boolean longToast) {
		if (longToast)	{ Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show(); }
		else			{ Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show(); }
	}
}
