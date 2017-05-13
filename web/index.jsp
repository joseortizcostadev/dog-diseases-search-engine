<%--
    Document   : index.jsp
    Created on : Apr 21, 2017, 3:34:52 PM
    Author     : Jose Ortiz Costa
    Purpose    : Final Project Search Engine class at SFSU
    Title      : Dog Diseases Reasearch Papers Search Engine.
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="Core.*"%>

<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
</head>

<div class="container">
    <h1> <font color=""><center>Canine Diseases Research Papers </center></font></h1>
    <p></p>
    <div class="col-md-3">
        <form class="navbar-form" role="search" id="navBarSearchForm" action="index.jsp" method="GET">
            <div class="input-group add-on">
                <font size="18"><input class="form-control" placeholder="Search a canine disease. e.g: Diabetis" name="srch-term" id="srch-term" type="text"></font>

                <div class="input-group-btn">
                    <button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>
                </div>


            </div>
            <p></p>
            <div style="width:1000px;">
                <input TYPE=checkbox name=expansion_mode VALUE=1> <font size="3">Select for recommending query expansion with term semantic relationship. Otherwise, will recommend expansion by term score  </font><BR>
            </div>
        </form>
        <p></p>
        <p></p>
        <%
            // initialize variables and get paths
            double score = 0.00;
            String docTitle = "";
            String docUrl = "";
            String docDescription = "";
            int termFrequency = 0;
            String queryExpansion = "";
            int docRanking = 0;
            String noResultsFound = "Sorry. There are no documents matches for this query";
            String researchDocsDir = session.getServletContext().getRealPath(Path.RESEARCH_DOCUMENTS_DIR);
            String indexDir = session.getServletContext().getRealPath(Path.INDEX_DIR);
            String otherDocuments = session.getServletContext().getRealPath(Path.OTHER_DOCUMENTS_DIR);
            String otherIndex = session.getServletContext().getRealPath(Path.OTHER_INDEX);
            String semanticIndex = session.getServletContext().getRealPath(Path.SEMANTIC_INDEX_PATH);
            /* Only remove comment if you want to build the index */
            // Research document index
            //Documents.buildIndex(researchDocsDir, indexDir, false); 
            // Reference documents index
            //Documents.buildIndex(otherDocuments, otherIndex, false);
            SearchEngine engine = new SearchEngine(indexDir);
            SearchEngine engineOtherDocuments = new SearchEngine(otherIndex);
            String query = request.getParameter("srch-term");
            String semanticOption = request.getParameter("expansion_mode");
            boolean isSemanticSelected = false;

            if (semanticOption != null && semanticOption.equals("1")) {
                isSemanticSelected = true;
            }
            DocumentMatched[] docsMatched = null;
            DocumentMatched[] docsMatchedOtherDocuments = null;
            String t = "";
            if (query != null) { // if query is not null start the search
                String records = "";
                docsMatched = engine.searchQuery(query);
                if (docsMatched != null) {
                    docsMatchedOtherDocuments = engineOtherDocuments.searchQuery(query);
                    String docDirTop = session.getServletContext().getRealPath(Path.RESEARCH_DOCUMENTS_DIR);
                    docDirTop = docDirTop + "/" + docsMatched[0].getDocumentTitle() + ".html";
                    QueryExpansion expansionTop = new QueryExpansion(query, docDirTop, 3, isSemanticSelected);
                    expansionTop.setSemanticIndexPath(semanticIndex);
                    queryExpansion = expansionTop.getQueryExpanded(3);
                    String[] allDocsPaths = new String[docsMatched.length];
                    int index = 0;
                    // Get prob terms using naive Bayes
                    NaiveBayesClassifier nbc = new NaiveBayesClassifier(session.getServletContext().getRealPath(Path.RESEARCH_DOCUMENTS_DIR), query);
                    Map<Integer, Double> classifiedDoc = nbc.getTopClassifiedDocId(2);

                    for (Map.Entry<Integer, Double> entry : QueryExpansion.entriesSortedByValues(classifiedDoc)) {

                        records += docsMatchedOtherDocuments[entry.getKey()].getDocumentTitle() + ":" + entry.getValue() + "  ";
                    }
                } else {
                    queryExpansion = "Expansion not posible, not document were matched in any of the index";
                }

        %>
        <div id="query-recomendation" style="width:1000px;">
            <h3><font color="grey">Need to get better results? Try this query: </font><font color="blue"><%=queryExpansion%></font></h3>
        </div>
        <hr style="width:1000px;">
        <h3 style="width:1000px;"> Research Papers found matching query: <%=query%> </h3>
        <div id="block-container">
            <%
                // Expand Query
                if (docsMatched != null) {
                    for (DocumentMatched doc : docsMatched) {
                        score = doc.getScore();
                        docTitle = doc.getDocumentTitle();
                        docUrl = doc.getUrl();
                        docRanking = doc.getRanking();
                        docDescription = doc.getDescription();
                        String docDir = session.getServletContext().getRealPath(Path.RESEARCH_DOCUMENTS_DIR);
                        docDir = docDir + "/" + docTitle + ".html";
                        QueryExpansion expansion = new QueryExpansion(query, docDir, 3, isSemanticSelected);
                        expansion.setSemanticIndexPath(semanticIndex);
                        termFrequency = expansion.getTermFrequency();


            %>
            <hr style="width:600px;">
            <div id="result-tag" style="width:1000px; ">
                <h4><font color="#0000ff"><a href=<%=docUrl%>><%=docRanking + ". " + docTitle%></a></font></h4>
                <h4><font color="green"><%=docUrl%></font></h4>
                <h4>Score: <%=score%></h4>
                <h4>Query Frequency: <%=termFrequency%></h4>
                <h5><font color="grey"><%=docDescription + "..."%></font></h5>
            </div>

            <%    }
                }%>
            <div id="recomendation-task">
                <h5><font color="grey"><%=records%></font></h5>
            </div>

            <%
                queryExpansion = "";
            } else {

            %>
            <div id="recomendation-task">
                <h5><font color="grey"><%=noResultsFound%></font></h5>
            </div>

            <%
                }


            %>
        </div>
        <style>
            #navBarSearchForm input[type=text]{width:800px !important;}
            #h4 input[type=text]{width:800px !important;}
            #h3 input[type=text]{width:800px !important;}
            #navBarSearchForm input[type=text]{margin: 0 auto;}

        </style>


    </div>


</div>
