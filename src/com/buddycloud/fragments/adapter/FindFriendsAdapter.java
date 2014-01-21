package com.buddycloud.fragments.adapter;

import java.util.Locale;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.buddycloud.R;
import com.buddycloud.fragments.GenericSelectableChannelsFragment;
import com.buddycloud.fragments.contacts.ContactMatcher;
import com.buddycloud.fragments.contacts.DeviceContactMatcher;
import com.buddycloud.fragments.contacts.FacebookContactMatcher;
import com.buddycloud.model.ModelCallback;

public class FindFriendsAdapter extends GenericChannelAdapter {

	private static final int IN_DEVICE = 1;
	private static final int FACEBOOK = 0;
	
	public static final String ADAPTER_NAME = "FIND_FRIENDS";
	
	public void configure(final GenericSelectableChannelsFragment fragment, View view) {
		
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                fragment.getActivity(),
                android.R.layout.select_dialog_item);
		
		Context context = fragment.getActivity();
		
        arrayAdapter.add(context.getString(R.string.contact_matching_facebook));
        arrayAdapter.add(context.getString(R.string.contact_matching_contact_list));
		
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(
        		fragment.getActivity());
        builderSingle.setTitle(context.getString(R.string.contact_matching_title));
        builderSingle.setAdapter(arrayAdapter, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ContactMatcher matcher = createMatcher(which);
				final String matcherName = matcher.getName();
				matcher.match(fragment.getActivity(), new ModelCallback<JSONArray>() {
					@SuppressLint("DefaultLocale")
					@Override
					public void success(JSONArray response) {
						for (int i = 0; i < response.length(); i++) {
							addChannel(matcherName.toUpperCase(Locale.getDefault()), 
									response.optJSONObject(i), fragment.getActivity());
						}
					}
					
					@Override
					public void error(Throwable throwable) {
						Toast.makeText(fragment.getActivity(), 
								throwable.getMessage(), Toast.LENGTH_LONG).show();
						builderSingle.show();
					}
				});
			}
		});
        builderSingle.show();
	}

	protected ContactMatcher createMatcher(int which) {
		ContactMatcher matcher = null;
		switch (which) {
		case FACEBOOK:
			matcher = new FacebookContactMatcher();
			break;
		case IN_DEVICE:
			matcher = new DeviceContactMatcher();
			break;
		default:
			break;
		}
		return matcher;
	}
	
}
