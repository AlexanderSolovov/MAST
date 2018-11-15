package com.rmsi.android.mast.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.CaptureDataMapActivity;
import com.rmsi.android.mast.activity.DataSummaryActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.adapter.SurveyListingAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.util.CommonFunctions;

public class CompletedSurveyFragment extends Fragment {
    Context context;
    SurveyListingAdapter adapter;
    List<Feature> features = new ArrayList<Feature>();
    public static int count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_survey_review_data_list, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        adapter = new SurveyListingAdapter(context, this, features, "completed");
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && DraftSurveyFragment.listChanged) {
            refreshList();
            Toast.makeText(context, R.string.refreshing_msg, Toast.LENGTH_SHORT).show();
            DraftSurveyFragment.listChanged = false;
        }
    }

    public void showPopupDraft(View v, Object object) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.survey_completed_options, popup.getMenu());
        int position = (Integer) object;
        final Long featureId = features.get(position).getId();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mark_as_draft:
                        markFeatureAsDraft(featureId);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void markFeatureAsDraft(final Long featureId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.draftFeatureMsg);

        alertDialogBuilder.setPositiveButton(R.string.btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Property property = DbController.getInstance(context).getProperty(featureId);
                        boolean result = DbController.getInstance(context).markFeatureAsDraft(property.getId());
                        if (result) {
                            getActivity().recreate();
                        } else {
                            String msg = getResources().getString(R.string.Error_updating_feature);
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void refreshList() {
        DbController db = DbController.getInstance(context);
        features.clear();
        features.addAll(db.fetchCompletedFeatures());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }

}
