package com.example.arcibald160.callblocker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arcibald160.callblocker.data.BlockListContract;
import com.example.arcibald160.callblocker.tools.CursorContactsHelper;

public class AddContactToBlockedList extends AppCompatActivity {

    private EditText mBlockedNumberView;
    private EditText mBlockedNameView;
    private Button mContactsButton;
    private Button mAddButton;

    private static final int PICK_CONTACT_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_to_blocked_list);

        mBlockedNumberView = (EditText) findViewById(R.id.number_input_id);
        mBlockedNameView = (EditText) findViewById(R.id.name_input_id);

        mContactsButton = (Button) findViewById(R.id.contacts_button_id);
        mAddButton = (Button) findViewById(R.id.add_blocked_button_id);

        // update / add
        if (getIntent().hasExtra(BlockListContract.BlockListEntry._ID)) {
            populateWithExistingData();
        }

        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset edit text
                mBlockedNumberView.setText("");
                mBlockedNameView.setText("");

                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT_CODE);
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mBlockedNumberView.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_number), Toast.LENGTH_LONG).show();
                    return;
                }

                // update / add
                if (getIntent().hasExtra(BlockListContract.BlockListEntry._ID)) {
                    updateBlockedList();
                } else {
                    addToBlockedList();
                }

                finish();
            }
        });
        // back navigation arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // get contact info if it is picked from native contact app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT_CODE) {

            if (resultCode == RESULT_OK) {
                Uri uriContact = data.getData();

                // get Cursor from picked contact
                Cursor cursorID = this.getContentResolver().query(
                        uriContact,
                        new String[]{ContactsContract.Contacts._ID},
                        null,
                        null,
                        null
                );

                // get string id of picked contact
                String contactID = null;
                if (cursorID.moveToFirst()) {
                    contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
                }
                cursorID.close();

                Cursor cursorPhone = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                        new String[]{contactID}, null);

                // get phone number from contact
                if (cursorPhone.moveToFirst()) {
                    String phoneNum = cursorPhone.getString(
                            cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    mBlockedNumberView.setText(phoneNum);
                }

                // querying contact data store
                Cursor cursor = this.getContentResolver().query(
                        uriContact,
                        null,
                        null,
                        null,
                        null
                );

                // get name from contact
                if (cursor.moveToFirst()) {
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    mBlockedNameView.setText(contactName);
                }
            }
        }
    }

    private ContentValues getDbValues() {
        // Defines an object to contain the new values to insert
        ContentValues values = new ContentValues();

        values.put(BlockListContract.BlockListEntry.COLUMN_NUMBER, mBlockedNumberView.getText().toString());

        if (!TextUtils.isEmpty(mBlockedNameView.getText().toString())) {
            values.put(BlockListContract.BlockListEntry.COLUMN_NAME, mBlockedNameView.getText().toString());
        }
        
        return values;
    }


    private void updateBlockedList() {
       
        int id = getIntent().getIntExtra(BlockListContract.BlockListEntry._ID, 0);
        Uri uri = BlockListContract.BlockListEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        int returnValue = getApplicationContext().getContentResolver().update(
                uri,
                getDbValues(),
                null,
                null
        );
    }

    private void addToBlockedList() {
        // Defines a new Uri object that receives the result of the insertion (debug purpose)
        Uri mNewUri;

        mNewUri = getApplicationContext().getContentResolver().insert(
                BlockListContract.BlockListEntry.CONTENT_URI,
                getDbValues()
        );
    }

    private void populateWithExistingData() {
        int id = getIntent().getIntExtra(BlockListContract.BlockListEntry._ID, 0);
        Cursor result = getApplicationContext().getContentResolver().query(
                BlockListContract.BlockListEntry.CONTENT_URI, null,
                "_id=?",
                new String[]{Integer.toString(id)},
                null
        );

        if (result != null) {
            result.moveToFirst();
            CursorContactsHelper cHelper = new CursorContactsHelper(result);

            mBlockedNameView.setText(cHelper.contactName);
            mBlockedNumberView.setText(cHelper.contactNumber);
        }
    }

    //    used for navigating back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
