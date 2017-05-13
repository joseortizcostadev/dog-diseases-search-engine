package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import Core.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<head>\n");
      out.write("    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css\">\n");
      out.write("</head>\n");
      out.write("\n");
      out.write("<div class=\"container\">\n");
      out.write("    <h1> <font color=\"\"><center>Canine Diseases Research Papers </center></font></h1>\n");
      out.write("    <p></p>\n");
      out.write("    <div class=\"col-md-3\">\n");
      out.write("        <form class=\"navbar-form\" role=\"search\" id=\"navBarSearchForm\" action=\"index.jsp\" method=\"GET\">\n");
      out.write("            <div class=\"input-group add-on\">\n");
      out.write("                <font size=\"18\"><input class=\"form-control\" placeholder=\"Search a canine disease. e.g: Diabetis\" name=\"srch-term\" id=\"srch-term\" type=\"text\"></font>\n");
      out.write("                <div class=\"input-group-btn\">\n");
      out.write("                    <button class=\"btn btn-default\" type=\"submit\"><i class=\"glyphicon glyphicon-search\"></i></button>\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("        </form>\n");
      out.write("        <p></p>\n");
      out.write("        <p></p>\n");
      out.write("        ");

            
            double score = 0.00;
            String docTitle = "";
            String docUrl = "";
            String docDescription = "";
            int docRanking = 0;
            String researchDocsDir = session.getServletContext().getRealPath("/research_documents");
            String indexDir = session.getServletContext().getRealPath("/index");
            /* Only remove comment if you want to build the index */
            // Documents.buildIndex(researchDocsDir, indexDir, false);
            SearchEngine engine = new SearchEngine(indexDir);
            String query = request.getParameter("srch-term");
            DocumentMatched [] docsMatched = null;
            if (query != null)
            {
                docsMatched = engine.searchQuery(query);
            
            for (DocumentMatched doc : docsMatched)
            {
                score = doc.getScore();
                docTitle = doc.getDocumentTitle();
                docUrl = doc.getDocumentUrl();
                docRanking = doc.getRanking();
                docDescription = doc.getDocumentDescription();
            
      out.write("\n");
      out.write("            <hr>\n");
      out.write("            <div id=\"result-tag\" style=\"width:1000px;\">\n");
      out.write("                <h4><font color=\"#0000ff\"><a href=");
      out.print(docUrl);
      out.write('>');
      out.print(docRanking + ". " + docTitle);
      out.write("</a></font></h4>\n");
      out.write("            <h4><font color=\"green\">");
      out.print(docUrl);
      out.write("</font></h4>\n");
      out.write("            <h4>Score: ");
      out.print(score);
      out.write("</h4>\n");
      out.write("            <h5><font color=\"grey\">");
      out.print(docDescription + "...");
      out.write("</font></h5>\n");
      out.write("            </div>\n");
      out.write("            \n");
      out.write("            ");
 }} 
      out.write("\n");
      out.write("            \n");
      out.write("            \n");
      out.write("            \n");
      out.write("        \n");
      out.write("        \n");
      out.write("        <style>\n");
      out.write("            #navBarSearchForm input[type=text]{width:700px !important;}\n");
      out.write("            #navBarSearchForm input[type=text]{margin: 0 auto;}\n");
      out.write("            \n");
      out.write("        </style>\n");
      out.write("        \n");
      out.write("\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("\n");
      out.write("</div>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
