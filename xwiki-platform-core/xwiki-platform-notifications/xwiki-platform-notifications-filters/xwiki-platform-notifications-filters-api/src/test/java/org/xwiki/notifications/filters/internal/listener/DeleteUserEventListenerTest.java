/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.notifications.filters.internal.listener;

import javax.inject.Named;
import javax.inject.Provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.notifications.NotificationException;
import org.xwiki.notifications.filters.internal.ModelBridge;
import org.xwiki.test.LogLevel;
import org.xwiki.test.junit5.LogCaptureExtension;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.doc.XWikiDocument;

import static ch.qos.logback.classic.Level.WARN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test of {@link DeleteUserEventListener}.
 *
 * @version $Id$
 * @since 14.5
 * @since 14.4.1
 * @since 13.10.7
 */
@ComponentTest
class DeleteUserEventListenerTest
{
    private static final DocumentReference REMOVED_USER_DOCUMENT_REFERENCE =
        new DocumentReference("xwiki", "XWiki", "RemovedUser");

    @InjectMockComponents
    private DeleteUserEventListener listener;

    @MockComponent
    @Named("cached")
    private Provider<ModelBridge> modelBridgeProvider;

    @Mock
    private ModelBridge modelBridge;

    @RegisterExtension
    LogCaptureExtension logCapture = new LogCaptureExtension(LogLevel.WARN);

    @Mock
    private XWikiDocument xWikiDocument;

    @BeforeEach
    void setUp()
    {
        when(this.modelBridgeProvider.get()).thenReturn(this.modelBridge);
        when(this.xWikiDocument.getDocumentReference()).thenReturn(REMOVED_USER_DOCUMENT_REFERENCE);
    }

    @Test
    void onEvent() throws Exception
    {
        this.listener.onEvent(null, this.xWikiDocument, null);
        verify(this.modelBridge).deleteFilterPreferences(REMOVED_USER_DOCUMENT_REFERENCE);
    }

    @Test
    void onEventException() throws Exception
    {
        doThrow(NotificationException.class).when(this.modelBridge)
            .deleteFilterPreferences(REMOVED_USER_DOCUMENT_REFERENCE);
        this.listener.onEvent(null, this.xWikiDocument, null);
        assertEquals(1, this.logCapture.size());
        assertEquals("Failed to delete notification preferences for user [xwiki:XWiki.RemovedUser]. "
            + "Cause: [NotificationException: ].", this.logCapture.getMessage(0));
        assertEquals(WARN, this.logCapture.getLogEvent(0).getLevel());
    }
}
