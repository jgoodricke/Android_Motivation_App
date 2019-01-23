package com.android.example.rewardify;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
* A dialog box for adding rewards
 */
public class RewardsDialogFragment extends DialogFragment {

	private View rootView;

	private OnCompleteListener mListener;

	// Required empty public constructor
	public RewardsDialogFragment() {
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment.
	 */
	public static RewardsDialogFragment newInstance(String title) {
		RewardsDialogFragment frag = new RewardsDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_add_reward, container);

		createListeners();

		return rootView;
	}

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

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**Interface to communicate with parent activity*/
	//Source: https://stackoverflow.com/questions/15121373/returning-string-from-dialog-fragment-back-to-activity
	public static interface OnCompleteListener {
		public abstract void onComplete(String result);
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

	public void cancelPressed(){
		this.dismiss();
	}

	//Process data when okay button is pressed
	public void okPressed(){
		//Get Values
		EditText NameET = (EditText) rootView.findViewById(R.id.name_et);
		String name = NameET.getText().toString();

		EditText repeatsET = (EditText) rootView.findViewById(R.id.points_et);
		String points = repeatsET.getText().toString();

		String result;

		if (validInput(name, points))
		{
			result = name + ","	+ points + "," + "active";
			this.mListener.onComplete(result);
			this.dismiss();
		}
	}

	/**Checks user input is valid*/
	private boolean validInput(String name, String points)
	{
		//Check if name field is blank
		if (name.equals("")) {
			makeToast("Error: Name Field is blank.", false);
			return false;
		}
		else if (false) {
			makeToast("Error: Name Field is blank.", false);
			return false;
		}
		//check if repeats is 0
		else if(points.equals("") || (points.matches("[0]+"))) {
			makeToast("Error: repeats can't be zero.", false);
			return false;
		}
		else{
			return true;
		}
	}

	/**
	 * Used to easily make a toast
	 **/
	private void makeToast(String message, boolean longToast) {
		if (longToast)	{ Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show(); }
		else			{ Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show(); }
	}


}
