package com.app.android.hashmap;

import java.util.Calendar;
import com.app.android.hashmap.fragment.SearchBarsFragment;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

@SuppressLint("DefaultLocale")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	SearchBarsFragment searchFrag;
	boolean isStartDate;
	public DatePickerFragment(SearchBarsFragment searchFrag, boolean isStartDate) 
	{
		super();
		this.searchFrag = searchFrag;
		this.isStartDate = isStartDate;
		
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @SuppressLint("DefaultLocale")
	public void onDateSet(DatePicker view, int year, int month, int day) 
    {
    	String d = String.format("%02d", day);
    	String m = String.format("%02d", (month+1));
    	    	
    	if(isStartDate)
    	{
    		searchFrag.setStartDate(year+"-"+m+"-"+d);
    	}
    	else
    	{
    		searchFrag.setEndDate(year+"-"+m+"-"+d);
    	}
    	
    }
}