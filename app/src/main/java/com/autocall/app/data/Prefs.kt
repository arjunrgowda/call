// data/Prefs.kt
package com.autocall.app.data

import android.content.Context

object Prefs {
    private const val NAME = "autocall_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_SUPABASE_URL = "supabase_url"
    private const val KEY_SUPABASE_ANON = "supabase_anon_key"
    private const val KEY_ENABLED = "enabled"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun getToken(ctx: Context) = prefs(ctx).getString(KEY_TOKEN, "") ?: ""
    fun getSupabaseUrl(ctx: Context) = prefs(ctx).getString(KEY_SUPABASE_URL, "") ?: ""
    fun getSupabaseAnon(ctx: Context) = prefs(ctx).getString(KEY_SUPABASE_ANON, "") ?: ""
    fun isEnabled(ctx: Context) = prefs(ctx).getBoolean(KEY_ENABLED, true)

    fun save(ctx: Context, token: String, supabaseUrl: String, supabaseAnon: String) {
        prefs(ctx).edit()
            .putString(KEY_TOKEN, token.trim())
            .putString(KEY_SUPABASE_URL, supabaseUrl.trim().trimEnd('/'))
            .putString(KEY_SUPABASE_ANON, supabaseAnon.trim())
            .apply()
    }

    fun setEnabled(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun isConfigured(ctx: Context): Boolean {
        return getToken(ctx).isNotEmpty() &&
               getSupabaseUrl(ctx).isNotEmpty() &&
               getSupabaseAnon(ctx).isNotEmpty()
    }
}
