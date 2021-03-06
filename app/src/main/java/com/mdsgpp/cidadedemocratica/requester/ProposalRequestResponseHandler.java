package com.mdsgpp.cidadedemocratica.requester;

import com.mdsgpp.cidadedemocratica.model.Proposal;
import com.mdsgpp.cidadedemocratica.persistence.EntityContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by andreanmasiro on 9/9/16.
 */
public class ProposalRequestResponseHandler extends RequestResponseHandler implements Comparator<Proposal> {

    EntityContainer<Proposal> proposalsContainer = EntityContainer.getInstance(Proposal.class);
    public static final String proposalsEndpointUrl = "http://cidadedemocraticaapi.herokuapp.com/api/v0/proposals";
    public static final String proposalsStateCountRelevanceEndpointUrl = "http://cidadedemocraticaapi.herokuapp.com/api/v0/states_proposals_count_and_relavance_sum";
    public static int nextPageToRequest = 1;

    private final String jsonProposalType = "Proposta";
    private final String proposalTitleKey = "titulo";
    private final String proposalContentKey = "descricao";
    private final String proposalIdKey = "id";
    private final String proposalRelevanceKey = "relevancia";
    private final String proposalUserIdKey = "user_id";
    private final String proposalSlugKey = "slug";
    private final String proposalStateAbbrevKey = "state_abrev";

    @Override
    public void onSuccess(int statusCode, Map<String, List<String>> headers, JSONArray response) {
        if (statusCode == 200) {

            ArrayList<Proposal> proposals = new ArrayList<Proposal>();

            for (int i = 0; i < response.length(); ++i) {

                try {
                    JSONObject topicJson = response.getJSONObject(i);

                    long id = topicJson.getLong(proposalIdKey);
                    String title = topicJson.getString(proposalTitleKey);
                    String content = topicJson.getString(proposalContentKey);
                    long relevance = topicJson.getLong(proposalRelevanceKey);
                    long userId = topicJson.getLong(proposalUserIdKey);
                    String slug = topicJson.getString(proposalSlugKey);
                    String stateAbbrev = topicJson.getString(proposalStateAbbrevKey);


                    Proposal proposal = new Proposal(id, title, content, relevance, userId, slug, stateAbbrev);

                    proposals.add(proposal);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(proposals, this);
            afterSuccess(proposals);
            proposalsContainer.addAll(proposals);
        }

    }

    @Override
    public void onFailure(int statusCode, Map<String, List<String>> headers, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, errorResponse);
        afterError(String.valueOf(statusCode));
    }

    @Override
    public int compare(Proposal p1, Proposal p2) {
        return p1.compareTo(p2);
    }

}