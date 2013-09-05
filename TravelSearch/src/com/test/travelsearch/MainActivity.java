package com.test.travelsearch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class MainActivity extends Activity {

	private Button mSearchButton;
	private AutoCompleteTextView mAutoStart;
	private AutoCompleteTextView mAutoDestination;
	private EditText mDateEditText;
	private Calendar myCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		myCalendar = Calendar.getInstance();		
		
		// Auto complete edit text's
		mAutoStart = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		mAutoStart.setHint("Enter departure city" );
		mAutoStart.setAdapter(new AutoCompleteAdapter(this, R.layout.listview_item));
		mAutoStart.setThreshold(2);
		
		mAutoDestination = 	(AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
		mAutoDestination.setHint("Enter destination city" );
		mAutoDestination.setAdapter(new AutoCompleteAdapter(this, R.layout.listview_item));
		mAutoDestination.setThreshold(2);		
		
		//Search button
		mSearchButton = (Button) findViewById(R.id.button1);
		mSearchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						v.getContext());
					alertDialogBuilder.setTitle("Search Results");
					alertDialogBuilder
						.setMessage("Search is not yet implemented!!")
						.setCancelable(false)
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
							}
						  });
		 
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
		});
		
		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

		    @Override
		    public void onDateSet(DatePicker view, int year, int monthOfYear,
		            int dayOfMonth) {
		        myCalendar.set(Calendar.YEAR, year);
		        myCalendar.set(Calendar.MONTH, monthOfYear);
		        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        updateEditText();
		    }

		};
		
		// Edit text for entering date
			mDateEditText = (EditText) findViewById(R.id.editText1);
			mDateEditText.setHint("Select date for travel");
			mDateEditText.setInputType(InputType.TYPE_NULL);
			updateEditText();
			mDateEditText.setOnClickListener(new View.OnClickListener() {

		        @Override
		        public void onClick(View v) {
		            new DatePickerDialog(v.getContext(), date, myCalendar
		                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
		                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		            
		        }
		    });
	}
	
	public void updateEditText() {
		String myFormat = "EEE, dd MMM yyyy"; 
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		mDateEditText.setText(sdf.format(myCalendar.getTime()));
	}
	
}
