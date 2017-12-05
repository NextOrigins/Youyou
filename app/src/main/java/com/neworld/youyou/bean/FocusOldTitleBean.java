package com.neworld.youyou.bean;

import com.neworld.youyou.view.NewTitleFocus;
import com.neworld.youyou.view.OldTitleFocus;

/**
 * Created by tt on 2017/8/8.
 */

public class FocusOldTitleBean implements OldTitleFocus {
    public String title;

    public FocusOldTitleBean(String title) {
        this.title = title;
    }
}
