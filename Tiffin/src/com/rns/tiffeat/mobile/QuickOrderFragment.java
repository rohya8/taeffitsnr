package com.rns.tiffeat.mobile;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.tiffeat.mobile.asynctask.ValidateQuickOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.bo.domain.PaymentType;

public class QuickOrderFragment extends Fragment implements OnClickListener,AndroidConstants{

	private RadioButton lunch,dinner,codpayment,onlinepayment; 
	private EditText lunchaddr;

	private Button proceed;
	private String lunchadress,dinneradress,bothaddress;
	private CustomerOrder customerOrder; 
	private TextView tiffindesc,name,emailid,phone,amount;
	private String customervendorname,customertiffindesc,customername,customeremailid,customerphone,customeramount;
	private View rootView;
	Context context;
	private Date lunchdate, dinnerdate;
	private DateFormat dateFormat;
	private Map<MealType,Date> availableMealType;

	public QuickOrderFragment(CustomerOrder customerOrder, Map<MealType, Date> availableMealType) {
		this.customerOrder=customerOrder;
		this.availableMealType=availableMealType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}


	private void getMealDate(Map<MealType, Date> availableMealType2) {

		if(availableMealType2.get(MealType.LUNCH)!=null)
		{
			lunchaddr.setVisibility(View.VISIBLE);
			lunchdate=availableMealType2.get(MealType.LUNCH);

			if(lunchdate!=null )
				lunch.setText("Lunch for ( " + dateFormat.format(lunchdate) +" )");
			lunch.setVisibility(View.VISIBLE);
		}
		else if(availableMealType2.get(MealType.DINNER)!=null)
		{
			lunchaddr.setVisibility(View.VISIBLE);
			dinnerdate=availableMealType2.get(MealType.DINNER);

			if(dinnerdate!=null )
				dinner.setText("Lunch for ( " + dateFormat.format(dinnerdate) +" )");
			dinner.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_quick_order, container, false);

		initialise();

		lunch.setOnClickListener(this);
		dinner.setOnClickListener(this);
		proceed.setOnClickListener(this);
		codpayment.setOnClickListener(this);
		onlinepayment.setOnClickListener(this);

		return rootView;

	}
	private void initialise( ) {


		lunch=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_lunch);
		dinner=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_dinner);
		codpayment=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_cashondelivery);
		onlinepayment=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_onlinepayment);

		lunchaddr=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_LunchAddress);
		tiffindesc=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_TiffinName);
		name=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Name);
		emailid=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Email);
		phone=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Phoneno);
		amount=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Price);
		proceed=(Button) rootView.findViewById(R.id.quick_order_screen_proceed_button);

		//String object=getActivity().getIntent().getExtras().getString("CustomerOrder");
		//customerOrder = new Gson().fromJson(object, CustomerOrder.class);

		customerData();
		getMealDate(availableMealType);
	}

	private void customerData() {

		tiffindesc.setText(customerOrder.getMeal().getTitle());
		name.setText(customerOrder.getCustomer().getName());
		emailid.setText(customerOrder.getCustomer().getEmail());;
		phone.setText(customerOrder.getCustomer().getPhone());
		amount.setText(customerOrder.getMeal().getPrice().toString());

//		if(MealType.BOTH.equals(customerOrder.getMealType()))
//		{
//			lunch.setVisibility(View.VISIBLE);
//			dinner.setVisibility(View.VISIBLE);
//
//			if(lunchdate!=null && dinnerdate!=null)
//			{
//				lunch.setText("Lunch for ( " + dateFormat.format(lunchdate) +" )");
//				dinner.setText("Dinner for ( " + dateFormat.format( dinnerdate) +" )");
//			}
//		}
//		else if(MealType.LUNCH.equals(customerOrder.getMealType()))
//		{
//			lunchaddr.setVisibility(View.VISIBLE);
//			if(lunchdate!=null )
//				lunch.setText("Lunch for ( " + dateFormat.format(lunchdate) +" )");
//			lunch.setVisibility(View.VISIBLE);
//		}
//		else if(MealType.DINNER.equals(customerOrder.getMealType()))
//		{
//			lunchaddr.setVisibility(View.VISIBLE);
//			dinner.setVisibility(View.VISIBLE);
//			if( dinnerdate!=null)
//				dinner.setText("Dinner for ( " + dateFormat.format( dinnerdate) +" )");
//			lunchaddr.setHint("Dinner Address");
//		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) 
		{
		case R.id.quick_order_screen_radioButton_lunch:
			dinner.setChecked(false);

			lunchaddr.setVisibility(View.VISIBLE);
			lunchaddr.setHint("Lunch Address");

			break;

		case R.id.quick_order_screen_radioButton_dinner:
			lunch.setChecked(false);

			lunchaddr.setVisibility(View.VISIBLE);
			lunchaddr.setHint("Dinner Address");
			break;

		case R.id.quick_order_screen_proceed_button:

			if(Validation.isNetworkAvailable(getActivity()))
			{
				Validation.checknetwork(getActivity());
			}
			else
			{
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),0);

				if(lunchaddr.getText().toString().equals("") )
					Toast.makeText(getActivity(), " Do not Leave Empty Field ", Toast.LENGTH_SHORT).show();
				else if(lunchaddr.getText().toString().length() <= 8 )
					Toast.makeText(getActivity(), " Enter Valid Address ", Toast.LENGTH_SHORT).show();
				else if(codpayment.isChecked()==false && onlinepayment.isChecked()==false)
					Toast.makeText(getActivity(), " Select A Payment Method ", Toast.LENGTH_SHORT).show();
				else if(dinner.isChecked()==false && lunch.isChecked()==false)
					Toast.makeText(getActivity(), " Select Address ", Toast.LENGTH_SHORT).show();
				else {
					prepareCustomerOrder();
					new ValidateQuickOrderAsyncTask(getActivity(), customerOrder).execute();
				}
			}
			break;

		case R.id.quick_order_screen_radioButton_cashondelivery:
			onlinepayment.setChecked(false);

			customerOrder.setPaymentType(PaymentType.CASH);
			break;

		case R.id.quick_order_screen_radioButton_onlinepayment:
			codpayment.setChecked(false);

			customerOrder.setPaymentType(PaymentType.NETBANKING);
			break;

		default:
			break;
		}

	}

	private void prepareCustomerOrder() 
	{
		customerOrder.setMealType(MealType.LUNCH);
		if(dinner.isChecked()) {
			customerOrder.setMealType(MealType.DINNER);
		}

		customerOrder.setAddress(lunchaddr.getText().toString());

		//customerOrder.setPaymentType(PaymentType.CASH);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}
}
