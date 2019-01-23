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
 *Tab for managing archived fragments
 */
public class TasksArchivedTabFragment extends Fragment {

	private View rootView;

	//Custom Row Adaptor Object
	private RowAdapter rAdapter;

	//DialogFragment
	public TasksArchivedDialogFragment editTaskDialogue;



	public TasksArchivedTabFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of this fragment
	 */
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
		rootView = inflater.inflate(R.layout.fragment_tasks_archived, container, false);
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
	 */
	
	//Show dialog for editing selected task
	private void showTaskDialog(String name) {
		FragmentManager fm = getFragmentManager();

		//TODO: Get Task
		Task task = ((TasksActivity)getActivity()).fileIO.getArchivedTask(name);

		editTaskDialogue = TasksArchivedDialogFragment.newInstance(task.name, task.total, task.frequency, task.xpPerPoint);

		//Show Dialogue
		editTaskDialogue.show(fm, "fragment_edit_task");
	}

	//Initialise list on startup
	private void initialiseList()
	{
		//TODO: change ID in archive view
		ListView list = (ListView) rootView.findViewById(R.id.archived_list_view);
		rAdapter = new RowAdapter();
		list.setAdapter(rAdapter);
	}

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
			super(getActivity(), R.layout.list_row_edit_task, R.id.title_tv, ((TasksActivity)getActivity()).fileIO.getArchivedTaskNames());
		}
		public View getView(int pos, View cView, ViewGroup parent) {
			final View row = super.getView(pos, cView, parent);

			//Get the current task
			Task currentTask = ((TasksActivity)getActivity()).fileIO.archivedTaskList.get(pos);

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
