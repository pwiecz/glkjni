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

typedef struct glk_schannel_struct schannel_t;

struct glk_schannel_struct {
    glui32 rock;

    jobject jchan;

    schannel_t *next, *prev;
    gidispatch_rock_t disprock;
};

/* Linked list of all sound channels. */
static schannel_t *gli_schanlist = NULL;

/* Returns the dispatch rock for SCHAN. */
gidispatch_rock_t gli_schan_get_disprock(schannel_t *schan)
{
    return schan->disprock;
}

/* Registers SCHAN with the dispatch layer. */
void gli_schan_set_disprock(schannel_t *schan)
{
    if (gli_register_obj) {
        schan->disprock = (*gli_register_obj)(schan, gidisp_Class_Schannel);
    } else {
        schan->disprock.ptr = NULL;
    }
}

schanid_t glk_schannel_iterate(schannel_t *chan, glui32 *rock)
{
    if (!chan) {
        chan = gli_schanlist;
    } else {
        chan = chan->next;
    }

    if (chan) {
        if (rock) {
            *rock = chan->rock;
        }
        return chan;
    } else {
        if (rock) {
            *rock = 0;
        }
        return NULL;
    }
}

schanid_t glk_schannel_create(glui32 rock)
{
    jobject jchan;
    schannel_t *schan;

    jchan = (*jni_env)->CallObjectMethod(GLK_M(CREATECHAN));
    if (jni_check_exc() || !jchan) {
        return NULL;
    }

    schan = (schannel_t *)gli_malloc(sizeof(schannel_t));

    schan->rock = rock;
    schan->jchan = jni_new_global(jchan);

    gli_schan_set_disprock(schan);

    schan->prev = NULL;
    schan->next = gli_schanlist;
    gli_schanlist = schan;
    if (schan->next) {
        schan->next->prev = schan;
    }

    return schan;
}

void glk_schannel_destroy(schannel_t *schan)
{
    schannel_t *prev, *next;

    if (!schan) {
        gli_strict_warning("schannel_destroy: invalid id.");
        return;
    }

    prev = schan->prev;
    next = schan->next;
    schan->prev = NULL;
    schan->next = NULL;

    if (prev) {
        prev->next = next;
    } else {
        gli_schanlist = next;
    }
    if (next) {
        next->prev = prev;
    }

    if (gli_unregister_obj) {
        (*gli_unregister_obj)(schan, gidisp_Class_Schannel, schan->disprock);
    }

    (*jni_env)->CallVoidMethod(GLK_M(CHANNELDESTROY), schan->jchan);
    jni_check_exc();
    (*jni_env)->DeleteGlobalRef(jni_env, schan->jchan);

    free(schan);
}

glui32 glk_schannel_get_rock(schannel_t *chan)
{
    if (!chan) {
        gli_strict_warning("schannel_get_rock: invalid id.");
        return 0;
    }
    return chan->rock;
}

glui32 glk_schannel_play(schannel_t *chan, glui32 snd)
{
    return glk_schannel_play_ext(chan, snd, 1, 0);
}

glui32 glk_schannel_play_ext(schannel_t *chan, glui32 snd, glui32 repeats,
    glui32 notify)
{
    jboolean jnotify, res;

    if (!chan) {
        gli_strict_warning("schannel_play: invalid id.");
        return FALSE;
    }
    if ((jint)snd < 0) {
        gli_strict_warning("schannel_play: resource num too large");
        return FALSE;
    }

    if ((jint)repeats < 0) {
        if ((jint)repeats != -1) {
            gli_strict_warning("schannel_play: repeat count too large");
            return FALSE;
        }
    } else if ((jint)repeats == 0) {
        glk_schannel_stop(chan);
        return TRUE;
    }

    jnotify = (notify ? JNI_TRUE : JNI_FALSE);

    res = (*jni_env)->CallBooleanMethod(
            INSTANCE_M(chan->jchan, GLKCHANNEL_PLAY),
            (jint)snd, (jint)repeats, jnotify);
    if (jni_check_exc()) {
        res = FALSE;
    }

    return res;
}

void glk_schannel_stop(schannel_t *chan)
{
    if (!chan) {
        gli_strict_warning("schannel_stop: invalid id.");
        return;
    }
    (*jni_env)->CallVoidMethod(
            INSTANCE_M(chan->jchan, GLKCHANNEL_STOP));
    jni_check_exc();
}

void glk_schannel_set_volume(schannel_t *chan, glui32 vol)
{
    if (!chan) {
        gli_strict_warning("schannel_set_volume: invalid id.");
        return;
    }
    if ((jint)vol < 0) {
        gli_strict_warning("schannel_set_volume: volume too loud");
    }

    (*jni_env)->CallVoidMethod(
            INSTANCE_M(chan->jchan, GLKCHANNEL_VOLUME),
            (jint)vol);
    jni_check_exc();
}

void glk_sound_load_hint(glui32 snd, glui32 flag)
{
    jboolean jflag = (flag ? JNI_TRUE : JNI_FALSE);

    if ((jint)snd < 0) {
        gli_strict_warning("sound_load_hint: resource num too large");
        return;
    }

    (*jni_env)->CallVoidMethod(GLK_M(SOUNDHINT), (jint)snd, jflag);
    jni_check_exc();
}
