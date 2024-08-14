package com.itaybs.expensemate.Utils;

import com.itaybs.expensemate.Model.Participant;

import java.util.ArrayList;

public class ParticipantUtils {
    public static Participant findParticipantById(ArrayList<Participant> participants, String participantId) {
        return participants.stream()
                .filter(p -> p.getId().equals(participantId)).findFirst().orElse(null);
    }

    public static Participant findParticipantByEmail(ArrayList<Participant> participants, String email) {
        return participants.stream()
                .filter(p -> p.getEmail().equals(email)).findFirst().orElse(null);
    }
}
