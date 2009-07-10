/* This file is a part of GlkJNI.
 * Copyright (c) 2009 Edward McCardell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <jni.h>
#include "glk.h"
#include "glkjni.h"
#include "jcall.h"

static void gli_process_event(event_t *event, glui32 type, glui32 id,
        glui32 val1, glui32 val2)
{
    winid_t win;

    if (id) {
        win = gli_window_by_id(id);
        gli_process_window_event(win, type, val1);
    } else {
        win = NULL;
    }

    if (event) {
        event->type = type;
        event->win = win;
        event->val1 = val1;
        event->val2 = val2;
    }
}

static void gli_select(event_t *event, int mid)
{
    jintArray jdata;
    jint *data;

    gli_windows_print();

    jdata = (*jni_env)->NewIntArray(jni_env, 4);
    if (!jdata) {
        jni_no_mem();
    }
    (*jni_env)->CallVoidMethod(jni_env, glkobj, jni_mcache[mid].mid, jdata);
    jni_check_exc();
    data = (*jni_env)->GetIntArrayElements(jni_env, jdata, NULL);
    if (!data) {
        gli_fatal("JNI error: could not access array");
    }

    gli_process_event(event, data[0], data[1], data[2], data[3]);

    (*jni_env)->DeleteLocalRef(jni_env, jdata);
}

void glk_select(event_t *event)
{
    gli_select(event, GLK_SELECT_METHOD);
}

void glk_select_poll(event_t *event)
{
    gli_select(event, GLK_POLL_METHOD);
}

void glk_request_timer_events(glui32 millisecs)
{
    if ((jint)millisecs < 0) {
        gli_strict_warning("request_timer_events: millisecs too large");
        return;
    }

    if (millisecs) {
        (*jni_env)->CallVoidMethod(GLK_M(REQUESTTIMER), (jint)millisecs);
    } else {
        (*jni_env)->CallVoidMethod(GLK_M(CANCELTIMER));
    }
    jni_check_exc();
}
