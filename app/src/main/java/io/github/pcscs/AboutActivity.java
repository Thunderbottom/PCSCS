package io.github.pcscs;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


public class AboutActivity extends AppCompatActivity {

    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element versionElement = new Element();
        versionElement.setTitle(BuildConfig.VERSION_NAME);
        versionElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click +=1;
                if (click % 5 == 0){
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.easter_egg), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView snackbarTV = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    snackbarTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    snackbar.show();
                }
            }
        });

        View aboutPage = new AboutPage(this)
                //.setDescription(R.string.about_desc)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .addItem(versionElement)
                .addGroup("Connect with us")
                .addEmail("chinmaydpai@gmail.com")
                .addWebsite("http://thunderbottom.github.io/")
                .addTwitter("pacmansyyu")
                .addInstagram("pacmansyyu")
                .addGitHub("thunderbottom/PCSCS")
                .addItem(getCopyRightsElement())
                .create();
        setContentView(aboutPage);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copyright), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_copyright);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}
