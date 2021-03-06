/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
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
package com.waz.zclient.core.stores.singleparticipants;

import com.waz.api.User;

import java.util.HashSet;
import java.util.Set;

public abstract class SingleParticipantStore implements ISingleParticipantStore {
    Set<SingleParticipantStoreObserver> singleParticipantObservers = new HashSet<>();

    @Override
    public void addSingleParticipantObserver(SingleParticipantStoreObserver singleParticipantObserver) {
        singleParticipantObservers.add(singleParticipantObserver);
    }

    @Override
    public void removeSingleParticipantObserver(SingleParticipantStoreObserver singleParticipantObserver) {
        singleParticipantObservers.remove(singleParticipantObserver);
    }

    protected void notifyUserHasBeenUpdated(User user) {
        for (SingleParticipantStoreObserver singleParticipantObserver : singleParticipantObservers) {
            singleParticipantObserver.onUserUpdated(user);
        }
    }
}
