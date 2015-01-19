/**
 * 
 */
package net.lxzhu.todaily;

import java.util.ArrayList;

import net.lxzhu.todaily.adapter.DropDownListAdapter;
import net.lxzhu.todaily.dao.Issue;
import net.lxzhu.todaily.dao.IssueDataContext;
import net.lxzhu.todaily.dto.DropDownItem;
import net.lxzhu.todaily.dto.DropDownItemBase;
import net.lxzhu.todaily.util.ToastUtil;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author RFIAS
 *
 */
public class IssueDetailsActivity extends Activity implements LocationListener {

	public static final String sExtraDataKeyIssueId = "issueid";
	public static final int sGPSUpdateMinTime = 0;
	public static final int sGPSUpdateMinDistance = 0;
	protected View basicFrame;
	protected View descriptionFrame;
	protected View slimFrame;
	protected Button basicButton;
	protected Button descriptionButton;
	protected Button slimFrameButton;
	protected ImageButton saveButton;
	protected ImageButton cancelButton;
	protected EditText titleText;
	protected EditText descriptionText;
	protected CheckBox includeLocationCheckBox;
	protected View locationPanel;
	protected TextView locationLatitudeTextView;
	protected TextView locationLongitudeTextView;
	protected TextView locationAltitudeTextView;
	protected TextView locationSpeedTextView;
	protected TextView locationTimeTextView;

	protected Button audioButton;
	protected long issueId;
	protected net.lxzhu.todaily.dao.Location location;
	protected Spinner importantLevelDropDownList;
	protected LocationManager locationManager;
	protected Handler asyncHandler;

	protected AudioRecordingUtil audioRecording;

	public void onCreate(Bundle savedInstanceState) {
		this.setContentView(R.layout.activity_issue_detail);
		super.onCreate(savedInstanceState);

		this.findViews();
		this.setupBasicButton();
		this.setupDescriptionButton();
		this.setupImportantLevels();
		this.setupSaveButton();
		this.setupCancelButton();
		this.setupSlimButton();
		this.getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setIcon(R.drawable.aves_arrow_left_48);
		this.getActionBar().setTitle("任务详情");
		this.bindIssueToUI();
		this.setupLocationManager();

		this.setupSlimAddAudioButton();
	}

	protected void onDestroy() {
		this.teardownLocationManager();
		super.onDestroy();
	}

	public IssueDetailsActivity getCurrentObject() {
		return this;
	}

	protected void findViews() {
		try {
			this.basicFrame = this.findViewById(R.id.activity_issue_detail_basic_frame);
			this.descriptionFrame = this.findViewById(R.id.activity_issue_detail_description_frame);
			this.slimFrame = this.findViewById(R.id.activity_issue_detail_slim_frame);

			this.basicButton = (Button) this.findViewById(R.id.activity_issue_detail_basic_button);
			this.descriptionButton = (Button) this.findViewById(R.id.activity_issue_detail_description_button);
			this.slimFrameButton = (Button) this.findViewById(R.id.activity_issue_detail_slim_button);

			this.saveButton = (ImageButton) this.findViewById(R.id.activity_issue_detail_save_button);
			this.cancelButton = (ImageButton) this.findViewById(R.id.activity_issue_detail_cancel_button);
			this.titleText = (EditText) this.findViewById(R.id.activity_issue_detail_title);
			this.descriptionText = (EditText) this.findViewById(R.id.activity_issue_detail_description);
			this.importantLevelDropDownList = (Spinner) this.findViewById(R.id.activity_issue_detail_important_level);

			this.includeLocationCheckBox = (CheckBox) this.findViewById(R.id.activity_issue_detail_include_location);
			this.locationPanel = this.findViewById(R.id.activity_issue_detail_location_panel);
			this.locationLatitudeTextView = (TextView) this.findViewById(R.id.activity_issue_detail_location_latitude);
			this.locationLongitudeTextView = (TextView) this
					.findViewById(R.id.activity_issue_detail_location_longitude);
			this.locationAltitudeTextView = (TextView) this.findViewById(R.id.activity_issue_detail_location_altitude);
			this.locationSpeedTextView = (TextView) this.findViewById(R.id.activity_issue_detail_location_speed);
			this.locationTimeTextView = (TextView) this.findViewById(R.id.activity_issue_detail_location_time);

			this.audioButton = (Button) this.findViewById(R.id.activity_issue_detial_slim_add_audio);

		} catch (Exception e) {
			ToastUtil.showText(this, e.getLocalizedMessage());
		}
	}

	private void setupBasicButton() {
		this.basicButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					basicFrame.setVisibility(View.VISIBLE);
					descriptionFrame.setVisibility(View.GONE);
					slimFrame.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.showText(getCurrentObject(), e.getLocalizedMessage());
				}

			}
		});
	}

	private void setupDescriptionButton() {
		this.descriptionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// TODO Auto-generated method stub
					basicFrame.setVisibility(View.GONE);
					slimFrame.setVisibility(View.GONE);
					descriptionFrame.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.showText(getCurrentObject(), e.getLocalizedMessage());
				}
			}
		});
	}

	private void setupSlimButton() {
		this.slimFrameButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					basicFrame.setVisibility(View.GONE);
					slimFrame.setVisibility(View.VISIBLE);
					descriptionFrame.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setupSlimAddAudioButton() {
		this.audioRecording = new AudioRecordingUtil(this);
		this.audioButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onAudioButtonClicked();
			}
		});
	}

	private void onAudioButtonClicked() {
		if (this.audioRecording.isRecording()) {
			this.audioRecording.stop();
			this.audioRecording.replay();
		} else {
			this.audioRecording.start(Environment.getDataDirectory() + "/mytest.wav");
		}
	}

	private void setupSaveButton() {
		final IssueDetailsActivity that = this;
		this.saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// TODO Auto-generated method stub
					if (!validateIssueData()) {
						Toast toast = Toast.makeText(getBaseContext(), "输入有误，请检查您的输入.", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					} else {
						Issue issue = collectIssueData();
						IssueDataContext dc = new IssueDataContext(getBaseContext());
						dc.saveIssue(issue);
						that.setResult(RESULT_OK);
						that.finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.showText(getCurrentObject(), e.getLocalizedMessage());
				}
			}
		});
	}

	private void setupCancelButton() {
		this.cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private boolean validateIssueData() {
		return true;
	}

	/**
	 * fetch issue object from database and bind its properties to ui elements
	 */
	private void bindIssueToUI() {
		try {
			Bundle extraBundle = this.getIntent().getExtras();

			if (extraBundle != null && extraBundle.containsKey(sExtraDataKeyIssueId)) {
				long issueId = extraBundle.getLong(sExtraDataKeyIssueId);
				IssueDataContext dc = new IssueDataContext(this.getApplicationContext());
				Issue issue = dc.findIssue(issueId);
				if (issue == null) {
					ToastUtil.showText(this, "failed to load specified issue");
					this.finish();
				} else {
					bindIssueToUI(issue);
				}
			}
		} catch (Exception e) {
			ToastUtil.showText(getCurrentObject(), e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * bind a specified issue object to ui elements
	 * 
	 * @param issue
	 */
	private void bindIssueToUI(Issue issue) {
		this.issueId = issue.getId();
		this.titleText.setText(issue.getTitle());
		this.descriptionText.setText(issue.getDescription());
		this.importantLevelDropDownList.setSelection(issue.getImportantLevel() - 1);
	}

	/**
	 * collect value from ui elements and construct an issue object.
	 * <p>
	 * this method does not store issue to database
	 * </p>
	 * 
	 * @return
	 */
	private Issue collectIssueData() {
		Issue retItem = null;
		try {
			Issue issue = new Issue();
			issue.setId(issueId);
			issue.setTitle(this.titleText.getText().toString());
			issue.setDescription(this.descriptionText.getText().toString());
			DropDownItem level = (DropDownItem) this.importantLevelDropDownList.getSelectedItem();
			issue.setImportantLevel(level.getValue());

			if (!this.includeLocation()) {
				issue.setLocation(new net.lxzhu.todaily.dao.Location());
			} else if (this.location != null) {
				issue.setLocation(this.location);
			}
			retItem = issue;
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.showText(getCurrentObject(), e.getLocalizedMessage());
		}

		return retItem;
	}

	protected boolean includeLocation() {
		return this.includeLocationCheckBox.isChecked();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return true;
	}

	private void setupImportantLevels() {
		try {
			ArrayList<DropDownItemBase> items = new ArrayList<DropDownItemBase>();
			items.add(new DropDownItemBase(1, "年度"));
			items.add(new DropDownItemBase(2, "季度"));
			items.add(new DropDownItemBase(3, "月度"));
			items.add(new DropDownItemBase(4, "周度"));
			items.add(new DropDownItemBase(5, "天度"));
			items.add(new DropDownItemBase(6, "时度"));
			items.add(new DropDownItemBase(7, "分度"));
			DropDownListAdapter<DropDownItemBase> adapter = new DropDownListAdapter<DropDownItemBase>(this, items);
			this.importantLevelDropDownList.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.showText(getCurrentObject(), e.getLocalizedMessage());
		}

	}

	@Override
	public void onLocationChanged(Location lastLocation) {
		if (lastLocation != null) {
			if (this.location == null) {
				this.location = new net.lxzhu.todaily.dao.Location();
			}
			this.location.setLatitude(lastLocation.getLatitude());
			this.location.setLongitude(lastLocation.getLongitude());
			this.updateLocationOnUI();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		ToastUtil.showText(this, "GPS is required to get location the issue.");
	}

	protected void setupLocationManager() {
		try {
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, sGPSUpdateMinTime,
						sGPSUpdateMinDistance, this);
				Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				this.onLocationChanged(lastLocation);
			} else {
				ToastUtil.showText(this, "GPS is not enabled");
			}
		} catch (Exception e) {
			ToastUtil.showText(this, e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	protected void teardownLocationManager() {
		if (this.locationManager != null) {
			this.locationManager.removeUpdates(this);
		}
	}

	protected void updateLocationOnUI() {
		if (this.location != null) {
			String latitude = String.format("%f", this.location.getLatitude());
			String longitude = String.format("%f", this.location.getLongitude());
			this.locationLatitudeTextView.setText(latitude);
			this.locationLongitudeTextView.setText(longitude);
		}
	}
}
