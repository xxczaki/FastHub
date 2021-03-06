package com.fastaccess.ui.modules.theme.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.main.donation.DonateActivity
import com.fastaccess.ui.widgets.SpannableBuilder

/**
 * Created by Kosh on 08 Jun 2017, 10:53 PM
 */

class ThemeFragment : BaseFragment<ThemeFragmentMvp.View, ThemeFragmentPresenter>(), ThemeFragmentMvp.View {

    val apply: FloatingActionButton by lazy { view!!.findViewById(R.id.apply) as FloatingActionButton }
    val toolbar: Toolbar by lazy { view!!.findViewById(R.id.toolbar) as Toolbar }

    private var primaryDarkColor: Int = 0
    private var theme: Int = 0
    private var themeListener: ThemeFragmentMvp.ThemeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        themeListener = context as ThemeFragmentMvp.ThemeListener
    }

    override fun onDetach() {
        themeListener = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int {
        return 0
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        apply.setOnClickListener {
            setTheme()
        }
        if (isPremiumTheme()) {
            toolbar.title = SpannableBuilder.builder().foreground(getString(R.string.premium_theme), Color.RED)
        }
        toolbar.setNavigationOnClickListener { activity.onBackPressed() }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        theme = arguments.getInt(BundleConstant.ITEM)
        val contextThemeWrapper = ContextThemeWrapper(activity, theme)
        primaryDarkColor = ViewHelper.getPrimaryDarkColor(contextThemeWrapper)
        val localInflater = inflater?.cloneInContext(contextThemeWrapper)
        val view = localInflater?.inflate(R.layout.theme_layout, container, false)
        return view
    }

    override fun providePresenter(): ThemeFragmentPresenter {
        return ThemeFragmentPresenter()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (themeListener != null) {
                themeListener!!.onChangePrimaryDarkColor(primaryDarkColor, theme == R.style.ThemeLight)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (PrefGetter.isProEnabled() || PrefGetter.isMidNightBlueThemeEnabled() || PrefGetter.isAmlodEnabled()) {
                themeListener?.onThemeApplied()
            }
        }
    }

    companion object {
        fun newInstance(style: Int): ThemeFragment {
            val fragment = ThemeFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ITEM, style)
                    .end()
            return fragment
        }
    }

    private fun setTheme() {
        when (theme) {
            R.style.ThemeLight -> {
                PrefHelper.set("appTheme", getString(R.string.light_theme_mode))
                themeListener?.onThemeApplied()
            }
            R.style.ThemeDark -> {
                PrefHelper.set("appTheme", getString(R.string.dark_theme_mode))
                themeListener?.onThemeApplied()
            }
            R.style.ThemeAmlod -> {
                if (PrefGetter.isAmlodEnabled() || PrefGetter.isProEnabled()) {
                    PrefHelper.set("appTheme", getString(R.string.amlod_theme_mode))
                    themeListener?.onThemeApplied()
                } else {
                    DonateActivity.start(this, getString(R.string.amlod_theme_purchase))
                }
            }
            R.style.ThemeMidNighBlue -> {
                if (PrefGetter.isAmlodEnabled() || PrefGetter.isProEnabled()) {
                    PrefHelper.set("appTheme", getString(R.string.mid_night_blue_theme_mode))
                    themeListener?.onThemeApplied()
                } else {
                    DonateActivity.start(this, getString(R.string.midnight_blue_theme_purchase))
                }
            }
        }
    }

    private fun isPremiumTheme(): Boolean = theme == R.style.ThemeAmlod || theme == R.style.ThemeMidNighBlue

}
