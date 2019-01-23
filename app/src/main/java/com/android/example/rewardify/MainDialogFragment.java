package com.android.example.rewardify;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainDialogFragment extends DialogFragment {

	private ArrayList<String> refreshTypes;

	private View rootView;

	private Spinner frequency_spinner;


	public MainDialogFragment() {
		// Empty constructor is required for DialogFragment
		// Make sure not to add arguments to the constructor
		// Use `newInstance` instead as shown below
	}

	public static MainDialogFragment newInstance(String title) {
		MainDialogFragment frag = new MainDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_add_task, container);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		createListeners();
		initiateSpinner();

		// Fetch arguments from bundle and set title
		String title = getArguments().getString("title", "Enter Name");
		getDialog().setTitle(title);
	}

	//Source: https://stackoverflow.com/questions/15121373/returning-string-from-dialog-fragment-back-to-activity
	public static interface OnCompleteListener {
		public abstract void onComplete(String result);
	}

	private OnCompleteListener mListener;

	// make sure the Activity implemented it
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mListener = (OnCompleteListener)activity;
		}
		catch (final ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
		}
	}

	public void cancelPressed(){
		this.dismiss();
	}

	public void okPressed(){
		//Get Values
		EditText NameET = (EditText) rootView.findViewById(R.id.name_et);
		String name = NameET.getText().toString();

		EditText repeatsET = (EditText) rootView.findViewById(R.id.repeats_et);
		String total = repeatsET.getText().toString();


		EditText expET = (EditText) rootView.findViewById(R.id.exp_et);
		String xpPerPoint = expET.getText().toString();


		String frequency = frequency_spinner.getSelectedItem().toString();

		String result;

		if (validInput(name, total, xpPerPoint))
		{
			result = name + ","
					+ "0" + ","
					+ total + ","
					+ xpPerPoint + ","
					+ frequency + ","
					+ "active";
			this.mListener.onComplete(result);
			this.dismiss();
		}
	}

	/**Checks user input is valid*/
	private boolean validInput(String name, String repeats, String exp)
	{
		//Check if name field is blank
		if (name.equals("")) {
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

	private void initiateSpinner()
	{
		frequency_spinner = (Spinner) rootView.findViewById(R.id.frequency_spinner);
		refreshTypes = new ArrayList<String>(Arrays.asList("daily", "weekly"));

		//Populate Spinner using adaptor and second list
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, refreshTypes);
		frequency_spinner.setAdapter(adapter);
	}

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
	}

	/**
	 * Used to easily make a toast
	 **/
	private void makeToast(String message, boolean longToast) {
		if (longToast)	{ Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show(); }
		else			{ Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show(); }
	}
}
