package com.android.example.rewardify;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;


/**
 *	Tab for managing active Tasks
 */
public class TasksActiveTabFragment extends Fragment {

	private View rootView;

	//Custom Row Adaptor Object
	private RowAdapter rAdapter;

	//DialogFragment
	public TasksActiveDialogFragment editTaskDialogue;


	public TasksActiveTabFragment() {
		// Required empty public constructor
	}


	public static TasksActiveTabFragment newInstance() {
		TasksActiveTabFragment fragment = new TasksActiveTabFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		rootView = inflater.inflate(R.layout.fragment_tasks_active, container, false);
		((TasksActivity)getActivity()).fileIO.importTasks();
		initialiseList();
		return rootView;
	}

	@Override
	public void onResume(){
		super.onResume();
		((TasksActivity)getActivity()).fileIO.importTasks();
		rAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}


	/**
	 * Interface Implementation to get result from dialogue fragment
	 * */
	private void showTaskDialog(String name) {
		FragmentManager fm = getFragmentManager();

		//TODO: Get Task
		Task task = ((TasksActivity)getActivity()).fileIO.getTask(name);

		editTaskDialogue = TasksActiveDialogFragment.newInstance(task.name, task.total, task.frequency, task.xpPerPoint);

		//Show Dialogue
		editTaskDialogue.show(fm, "fragment_edit_task");
	}
	
	//initialise list on startup
	private void initialiseList()
	{
		//TODO: change ID in archive view
		ListView list = (ListView) rootView.findViewById(R.id.active_list_view);
		rAdapter = new RowAdapter();
		list.setAdapter(rAdapter);
	}

	//Gets data from parent activity when editing is complete
	public void editingComplete()
	{
		initialiseList();
		rAdapter.notifyDataSetChanged();
	}

	/**
	 * Custom row adapter
	 **/
	class RowAdapter extends ArrayAdapter<String> {
		public RowAdapter() {
			super(getActivity(), R.layout.list_row_edit_task, R.id.title_tv, ((TasksActivity)getActivity()).fileIO.getTaskNames());
		}
		public View getView(int pos, View cView, ViewGroup parent) {
			final View row = super.getView(pos, cView, parent);

			//Get the current task
			Task currentTask = ((TasksActivity)getActivity()).fileIO.activeTaskList.get(pos);

			//Set onclick Listeners for the buttons
			final String name = currentTask.name;
			ImageButton incrementButton = row.findViewById(R.id.edit_button);
			incrementButton.setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View view) {
							showTaskDialog(name);
						}
					});

			return row;
		}
	}
}
