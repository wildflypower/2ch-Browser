package ua.in.quireg.chan.mvp.routing.commands;

import ru.terrakok.cicerone.commands.Command;

/**
 * Created by Arcturus Mengsk on 12/20/2017, 6:28 AM.
 * 2ch-Browser
 */

public class NavigateBoardsList implements Command {

    private String mWebsite;

    public NavigateBoardsList(String mWebsite) {
        this.mWebsite = mWebsite;
    }

    public String getWebsite() {
        return mWebsite;
    }
}
