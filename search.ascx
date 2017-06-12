<%@ control language="C#" autoeventwireup="true" inherits="controls_search" CodeFile="search.ascx.cs"  %>


    <script type="text/javascript">
    function searchTop(event)
    {
        var key=e.keyCode || e.which;
        if (key==13){
            document.location = '<%= ConfigurationSettings.AppSettings["sWebRoot"].ToString()%>home.aspx?page=search&key=' + document.getElementById('txtKeyword').value;
        }
    }
    </script>
 
    <div class="box-video">
    <div class="breadcrum-clip">
    <%=mess%>
     </div>
        <div class="box-list-video">   
        <asp:Label ID="lbInfo" runat="server" Text="" ForeColor=Red></asp:Label>
        
    <div class="box-list-video"> 

    <asp:Repeater ID="dgrCommon" runat="server">
	    <ItemTemplate>
          <div  class="vi-row"> 
            <%--<a href="<%# VatLid.Rewriter.rewriteVideo(Container.DataItem) %>" title="<%# Eval("name")%>">--%>
            <a href="<%# Server.HtmlEncode( Eval("link").ToString())%>" title="<%#Server.HtmlEncode( Eval("name").ToString())%>">
            
            <img src="<%#Server.HtmlEncode( Eval("image").ToString())%>" class="vi-img" alt="<%#Server.HtmlEncode(Eval("name").ToString())%>"/>
            </a>
            
            <h2 class="vi-name">
                <a href="<%#Server.HtmlEncode( Eval("link").ToString())%>" title="<%#Server.HtmlEncode(Eval("name").ToString())%>"> 
                <%#Server.HtmlEncode(Eval("name").ToString())%>
                </a>
            </h2>
          </div>
        </ItemTemplate>
    </asp:Repeater> 	

    </div>
        
        
        
          <asp:Panel ID="pnResults" runat="server"></asp:Panel>	
	        <%if (strPageLink != "")
           { %>
	        <div class="pagination clear">  
                    <%= strPageLink%>
            </div>
            <%} %>
        </div>
    </div>
    
<asp:Panel ID="pnCliphot" runat="server"></asp:Panel>

