package com.rns.tiffeat.mobile.asynctask;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rns.tiffeat.mobile.QuickOrderFragment;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.ScheduledOrderFragment;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class LoginAsyncTask extends AsyncTask<String, String, String>
{

	private FragmentActivity mlogin;
	private ProgressDialog progressdialog;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String result1;
	private Map<MealType,Date> availableMealType;

	public LoginAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2) 
	{
		mlogin=activity;
		customerOrder=customerOrder2;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressdialog = new ProgressDialog(mlogin);
		progressdialog.setMessage("Please Wait...");
		progressdialog.setTitle("Checking  Details ");
		progressdialog.setCancelable(false);
		progressdialog.show();
	}

	@Override
	protected String doInBackground(String... params) 
	{
		String resultLogin =  CustomerServerUtils.customerLogin(customerOrder.getCustomer());		

		//String resultRegistration =  CustomerServerUtils.customerRegistration(customerOrder.getCustomer());
		try 
		{
			customerlogin = new Gson().fromJson(resultLogin, Customer.class);
			if(customerlogin.getEmail().toString().equals(customerOrder.getCustomer().getEmail().toString()) )
			{
				if(customerlogin.getPassword().toString().equals(customerOrder.getCustomer().getPassword().toString()))
				{
					customerOrder.setCustomer(customerlogin);
					CustomerUtils.storeCurrentCustomer(mlogin, customerlogin);
				}
				else
					Toast.makeText(mlogin, "Please Check Your Password ", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(mlogin, "Please Check Your Email ", Toast.LENGTH_SHORT).show();

		} 
		catch (Exception e) 
		{					// com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING
			return resultLogin;
		}

		result1 =  CustomerServerUtils.customerGetMealAvailable(customerOrder);		// ResourceAccessException

		Type typeMap=new TypeToken<Map<String,Object>>(){}.getType();

		Map<String, Object> customerorderavail = new HashMap<String, Object>();

		customerorderavail=new Gson().fromJson(result1, typeMap);


		String customerOrderString = (String) customerorderavail.get("customerOrder");
		//customerOrder=(String) customerorderavail.get("customerOrder");
		availableMealType= (Map<MealType, Date>) customerorderavail.get("mealType");
		customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);

		return resultLogin; 

	}

	@Override
	protected void onPostExecute(String result) 
	{
		super.onPostExecute(result);
		progressdialog.dismiss();
		if(customerlogin == null) 
		{
			Toast.makeText(mlogin, "Login failed due to :" + result, Toast.LENGTH_LONG).show();
			return;
		}
		customerOrder.setCustomer(customerlogin);
		nextActivity(); 
	}

	private void nextActivity() 
	{

		//		String customerOrderobj=new Gson().toJson(customerOrder, CustomerOrder.class);

		Fragment fragment = null;		
		if(customerOrder.getMealFormat().equals(MealFormat.QUICK))
			fragment = new QuickOrderFragment(customerOrder,availableMealType);
		else if(customerOrder.getMealFormat().equals(MealFormat.SCHEDULED))
			fragment = new ScheduledOrderFragment(customerOrder,availableMealType);	


		CustomerUtils.nextFragment(fragment,mlogin.getSupportFragmentManager(),false);

	}


}
