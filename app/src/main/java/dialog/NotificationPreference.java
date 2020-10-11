package dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.barberme.R;

public class NotificationPreference extends Preference {


    public NotificationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayoutResource(R.layout.preference_notifications);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        CheckBox checkBox = (CheckBox) holder.findViewById(R.id.check_box_notif);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {

                    }
                }
        });
    }
}
