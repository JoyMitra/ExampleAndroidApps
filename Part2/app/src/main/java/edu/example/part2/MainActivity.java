package edu.example.part2;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.example.part2.Constant.Constant;
import edu.example.part2.model.Contact;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{"android.permission.READ_CONTACTS", "android.permission.WRITE_EXTERNAL_STORAGE"}
                        , Constant.REQ_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.REQ_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            /*
            If permission to read contacts and write to external storage is granted
            then the app saves all contacts in a zipped file.
            This operation happens on an io thread in background via the observable instance
             */
            final io.reactivex.Observable<Boolean> observable = io.reactivex.Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                    emitter.onNext(writeContacts());
                    emitter.onComplete();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.d(Constant.LOG_TAG, "Subscription done");
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.dispMsg, Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), R.string.dispMsgFail, Snackbar.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(Constant.LOG_TAG, e.getMessage());
                    throw new RuntimeException(e);
                }

                @Override
                public void onComplete() {
                    Log.d(Constant.LOG_TAG, "contacts backup operation done");
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), R.string.permMsg, Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Zips contacts information and
    stores the zipped file in the Documents folder of ExternalStorage
     */
    private Boolean writeContacts() {
        if (isExternalStorageWritable()) {
            String data = makeAllContactsString();
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(getDir("Part2"), "main.zip")))) {
                ZipEntry e = new ZipEntry("test.csv");
                zos.putNextEntry(e);
                zos.write(data.getBytes(), 0, data.getBytes().length);
                zos.closeEntry();
                Log.d(Constant.LOG_TAG, "Contacts saved successfully");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File getDir(String dir) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dir);
        file.mkdirs();
        return file;
    }

    /*
    Converts all contacts to string that can be written to a csv file
     */
    private String makeAllContactsString() {
        List<Contact> contacts = getAllContacts();
        StringBuffer sb = new StringBuffer();
        for (Contact contact :
                contacts) {
            String name = contact.getName();
            sb.append(name + ",");
            List<String> phoneNos = contact.getPhoneNumbers();
            if (phoneNos != null) {
                for (String phoneNo :
                        phoneNos) {
                    sb.append(phoneNo + ",");
                }
            }
            List<String> emailids = contact.getEmailid();
            if (emailids != null) {
                for (String emailid :
                        emailids) {
                    sb.append(emailid + ",");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /*
    Retrieves all contacts from the user's contact list
     */
    private List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        Contact contact;
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contact = new Contact();
                    contact.setId(id);
                    contact.setName(name);
                    Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},
                            null);
                    if (phoneCursor != null && phoneCursor.getCount() > 0) {
                        List<String> phoneNos = new ArrayList<>();
                        while (phoneCursor.moveToNext()) {
                            String phoneNo = phoneCursor.getString(phoneCursor.
                                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNos.add(phoneNo);
                        }
                        contact.setPhoneNumbers(phoneNos);
                        phoneCursor.close();
                    }
                    Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ? ", new String[]{id},
                            null);
                    if (emailCursor != null && emailCursor.getCount() > 0) {
                        List<String> emails = new ArrayList<>();
                        while (emailCursor.moveToNext()) {
                            String emailid = emailCursor.getString(emailCursor.
                                    getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                            emails.add(emailid);
                        }
                        contact.setEmailid(emails);
                        emailCursor.close();
                    }
                    contactList.add(contact);
                }
            }
            cursor.close();
        }
        return contactList;
    }
}
