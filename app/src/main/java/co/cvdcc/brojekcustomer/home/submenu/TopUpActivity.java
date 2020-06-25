package co.cvdcc.brojekcustomer.home.submenu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;

import co.cvdcc.brojekcustomer.utils.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputLayout;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.user.TopupRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.TopupResponseJson;
import co.cvdcc.brojekcustomer.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TopUpActivity extends AppCompatActivity {

    private static final String TAG = TopUpActivity.class.getSimpleName();

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;
    String bukti;
    private String bankName = "";
    TopUpActivity activity;
//    private Uri file;


    @BindView(R.id.pemilikRekening)
    EditText name;
    @BindView(R.id.nomorRekening)
    EditText accountNumber;
    @BindView(R.id.nominalTransfer)
    EditText nominal;
    @BindView(R.id.spinBank)
    Spinner spinner;
    @BindView(R.id.butUploadBukti)
    TextView upload;
    @BindView(R.id.butTopup)
    TextView topup;
    @BindView(R.id.other_bank_layout)
    TextInputLayout otherBankLayout;
    @BindView(R.id.other_bank)
    EditText otherBank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        ButterKnife.bind(this);
        activity = TopUpActivity.this;
        final User userLogin = MangJekApplication.getInstance(this).getLoginUser();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            upload.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//                Toast.makeText(activity, "Index : "+i, Toast.LENGTH_SHORT).show();
                if (i != 3) {
                    bankName = spinner.getSelectedItem().toString();
                    otherBankLayout.setVisibility(GONE);
                } else {
                    otherBankLayout.setVisibility(VISIBLE);
                    otherBank.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        otherBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bankName = otherBank.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nominal.addTextChangedListener(Utility.currencyTW(nominal));

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
//                take_photo();
            }
        });

        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTopUp();

//                JSONObject jVer = new JSONObject();
//                try {
//                    jVer.put("id", userLogin.getId());
//                    jVer.put("no_rekening", accountNumber.getText().toString());
//                    jVer.put("jumlah", nominal.getText().toString());
//                    jVer.put("atas_nama", name.getText().toString());
//                    jVer.put("bank", bankName);
//                    jVer.put("bukti", bukti);
//
//                    verifikasiTopup(jVer);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        });

    }


    private void submitTopUp() {
        final ProgressDialog pd = showLoading();

        User user = MangJekApplication.getInstance(this).getLoginUser();
        TopupRequestJson request = new TopupRequestJson();
        request.id = user.getId();
        request.atas_nama = name.getText().toString();
        request.no_rekening = accountNumber.getText().toString();
        request.jumlah = getNominal();
        request.bank = bankName;
        request.bukti = bukti;


        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.topUp(request).enqueue(new Callback<TopupResponseJson>() {
            @Override
            public void onResponse(Call<TopupResponseJson> call, Response<TopupResponseJson> response) {
                if (response.isSuccessful()) {
                    pd.dismiss();

                    if (response.body().message.equals("success")) {
                        Toast.makeText(activity, "Terima kasih. Verifikasi akan segera diproses..", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(activity, "Verifikasi bermasalah..", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopupResponseJson> call, Throwable t) {
                t.printStackTrace();
                pd.dismiss();
                Toast.makeText(TopUpActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getNominal() {
        String originalString = nominal.getText().toString();

        Long longval;
        if (originalString.contains(".")) {
            originalString = originalString.replaceAll("[$.]", "");
        }
        if (originalString.contains(",")) {
            originalString = originalString.replaceAll(",", "");
        }
        if (originalString.contains("Rp ")) {
            originalString = originalString.replaceAll("Rp ", "");
        }
        if (originalString.contains("Rp")) {
            originalString = originalString.replaceAll("Rp", "");
        }
        if (originalString.contains("R")) {
            originalString = originalString.replaceAll("R", "");
        }
        if (originalString.contains("p")) {
            originalString = originalString.replaceAll("p", "");
        }
        if (originalString.contains(" ")) {
            originalString = originalString.replaceAll(" ", "");
        }

        return originalString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                upload.setEnabled(true);
            }
        }
    }

//    private void take_photo(){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        file = Uri.fromFile(getOutputMediaFile());
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
//
//        startActivityForResult(intent, TAKE_PICTURE);
//    }

//    private static File getOutputMediaFile(){
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MangJek");
//
//        if (!mediaStorageDir.exists()){
//            if (!mediaStorageDir.mkdirs()){
//                return null;
//            }
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        return new File(mediaStorageDir.getPath() + File.separator +
//                "IMG_MangJek_"+ timeStamp + ".jpg");
//    }

    public void takePhoto() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Invoice_" + timeStamp;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), imageFileName);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
        } else {
            File file = new File(photo.getPath());
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileProvider", file);
            imageUri = photoUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {

                    activity.getContentResolver().notifyChange(imageUri, null);
                    ContentResolver cr = activity.getContentResolver();
                    Bitmap bitmap;
                    try {
//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                                android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
//                        Cursor cursor = cr.query(imageUri,
//                                filePathColumn, null, null, null);
//                        cursor.moveToFirst();
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String imgDecodableString = cursor.getString(columnIndex);
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
//                        bitmap = decodeFile(imgDecodableString, 200);
                        Log.d("after_comppres", String.valueOf(bitmap.getByteCount()));
                        bukti = compressJSON(bitmap);
                        if (!bukti.equals("")) {
                            ImageView centang = (ImageView) activity.findViewById(R.id.centang);
                            centang.setVisibility(VISIBLE);

                        }

                    } catch (Exception e) {
                        Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                }
                break;
            default:
                break;
        }
    }

//    private void verifikasiTopup(JSONObject jVer){
//
//        final ProgressDialog pd= showLoading();
//        Log.d("JVER", jVer.toString());
//
//        HTTPHelper.getInstance(activity).verifikasiTopUp(jVer, new NetworkActionResult() {
//            @Override
//            public void onSuccess(JSONObject obj) {
//                try {
//                    pd.dismiss();
//                    if(obj.getString("message").equals("success")){
//                        Toast.makeText(activity, "Terima kasih. Verifikasi akan segera diproses..", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(activity, "Verifikasi bermasalah..", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(String message) {
//
//            }
//
//            @Override
//            public void onError(String message) {
//                pd.dismiss();
//                showWarning();
//
//            }
//        });
//
//    }

//    private MaterialDialog showWarning() {
//        final MaterialDialog md = new MaterialDialog.Builder(activity)
//                .title("Connection problem.")
//                .content("Please try again.")
//                .icon(new IconicsDrawable(activity)
//                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
//                        .color(Color.YELLOW)
//                        .sizeDp(24))
//                .positiveText("Close")
//                .positiveColor(Color.DKGRAY)
//                .show();
//
//        View positive = md.getActionButton(DialogAction.POSITIVE);
//
//        positive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                md.dismiss();
//            }
//        });
//        return md;
//    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    public String compressJSON(Bitmap bmp) {
        byte[] imageBytes0;
        ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos0);
        imageBytes0 = baos0.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes0, Base64.DEFAULT);
        return encodedImage;
    }

}
