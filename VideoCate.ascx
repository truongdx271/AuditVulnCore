<%@ control language="C#" autoeventwireup="true" EnableViewState="false" inherits="video_VideoHome" CodeFile="VideoCate.ascx.cs" %>
<%@ Import Namespace="VatLid" %>

<%@ Register src="../adv/BoxAdvB2Home.ascx" tagname="BoxAdvB2Home" tagprefix="uc1" %>
<%@ Register src="../adv/BoxAdvB3Home.ascx" tagname="BoxAdvB3Home" tagprefix="uc2" %>

<%if (ConfigurationSettings.AppSettings["domain_web"].ToString()=="m.onbox.vn")
  {%>
    <%if (cid == 4)
              {%>
               <uc1:BoxAdvB2Home ID="BoxAdvB2Home1" runat="server" />
   
                
              <%} %>
    <%if (cid == 10)
              {%>
                
              <uc2:BoxAdvB3Home ID="BoxAdvB3Home1" runat="server" />
              <%} %>
              <%} %>
<div class="box-video funny">
<h3 class="h3-video">
<%if (label != 0)
  { %>
<a class="lnk-tab-left" href='<%=Server.HtmlEncode(categoryURL.ToString()) %>.html'><img  src="<%=Server.HtmlEncode(cimg.ToString()) %>"/>
<%=Server.HtmlEncode(CategoryName.ToString())%>
</a>

<%}
  else
  {
       %>
       
       <a class="lnk-tab-left" href='<%= VatLid.Rewriter.rewriteChannels(CategoryName,CNID) %>.html'>
       <img  src="<%=Server.HtmlEncode(cimg.ToString()) %>"/>
<%=Server.HtmlEncode(CategoryName.ToString())%>
</a>
       <%} %>
 
<%if (label == 0)
  {
      if (MSISDN.Length >= 5)
      {%>
                <script type="text/javascript">
                    function setFollow(cid, StatusFollow) {

                        $.ajax({
                            type: "POST",
                            data: { 'cid': cid, 'token': '<%=csrftoken%>', 'StatusFollow': StatusFollow },
                            url: 'api/setFollow.ashx',
                            success: function(result) {
                                if (result == "true") {                                  
                                    document.getElementById('noFollow' + cid).style.display = "inline-block";
                                    document.getElementById('setFollow' + cid).style.display = "none";
                                }
                            }
                        });
                    }

                    function NoFollow(cid, StatusFollow) {

                        $.ajax({
                            type: "POST",
                            data: { 'cid': cid, 'token': '<%=csrftoken%>', 'StatusFollow': StatusFollow },
                            url: 'api/setFollow.ashx',
                            success: function(result) {
                                if (result == "true") {                                   
                                    document.getElementById('noFollow' + cid).style.display = "none";
                                    document.getElementById('setFollow' + cid).style.display = "inline-block";                                   
                                }
                            }
                        });
                    }
                    
                </script>
                
              <%if (StatusFollow == 0)
                { %>
              <a onclick="setFollow(<%=Server.HtmlEncode(cid.ToString()) %>,<%=Server.HtmlEncode(StatusFollow.ToString()) %>);"  id="setFollow<%=Server.HtmlEncode(cid.ToString()) %>"  style="display:block;" class="lnk-tab-right" >
                      <span > Theo dõi</span> 
                        </a>
                        <a onclick="NoFollow(<%=Server.HtmlEncode(cid.ToString()) %>,<%=Server.HtmlEncode(StatusFollow.ToString()) %>);" style="display:none;"  id="noFollow<%=Server.HtmlEncode(cid.ToString()) %>"  class="lnk-tab-right followed" >
                                  <span >Đã theo dõi</span> 
                                    </a>
                       <%}
                else
                    if (StatusFollow == 1)
                    { %>
      
       <a onclick="setFollow(<%=Server.HtmlEncode(cid.ToString()) %>,<%=Server.HtmlEncode(StatusFollow.ToString()) %>);"  id="setFollow<%= Server.HtmlEncode(cid.ToString()) %>"  style="display:none;" class="lnk-tab-right" >
                      <span > Theo dõi</span> 
                        </a>
                        
              <a onclick="NoFollow(<%=Server.HtmlEncode(cid.ToString()) %>,<%=Server.HtmlEncode(StatusFollow.ToString()) %>);" style="display:block;"  id="noFollow<%=Server.HtmlEncode(cid.ToString()) %>"  class="lnk-tab-right followed" >
                                  <span >Đã theo dõi</span> 
                                    </h3>
   
    <h3>
                                    </a>
                                  
                        <%} %>
                        
                
                        
                    <%--    
<asp:Button ID="btnFollow" class="lnk-tab-right" runat="server" Text="Theo dõi" 
        onclick="btnFollow_Click"  />--%>
        
        
        
<%}
else
      {%>
      <a href="home.aspx?page=actionlogin"  id="setFollow_noMsisdn"  style="display:block;" class="lnk-tab-right" >
                      <span > Theo dõi</span> 
                        </a>
          
  <%}} %>
<%--<link href="../css/style_wap.css" rel="stylesheet" type="text/css" />--%>
</h3>
<div class="abc">

<div class="wrap-chanel-xemdi">
    
    <asp:Repeater ID="dgrCommon" runat="server">
        <ItemTemplate>
         
            <div class="chanel-item">
                <div class="wrap-chanel-item">
                    <a title="<%#Server.HtmlEncode( Eval("ItemName").ToString())%>" href="<%# VatLid.Rewriter.rewriteVideo(Container.DataItem) %>"
                        class="chanel-lnk-img">
                        <img alt="<%# Server.HtmlEncode(Eval("ItemName").ToString())%>" 
                        data-original="<%# VatLid.DAL.fixLinkImageVideo(VatLid.DAL.ReplaceLinkMediaVip(Server.HtmlEncode(Eval("ItemImage").ToString())))%>" class="lazy img-responsive"
                        src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAANSURBVBhXYzh8+PB/AAffA0nNPuCLAAAAAElFTkSuQmCC">
                    </a>
                    <div class="chanel-info">
                        <h3 class="chanel-info-h3">
                            <a href="<%# VatLid.Rewriter.rewriteVideo(Container.DataItem) %>">
                                <%#Server.HtmlEncode( Eval("ItemName").ToString())%></a>
                        </h3>
                        <span class="chanel-view">
                            <%#Server.HtmlEncode( Eval("IsView").ToString())%>
                            lượt xem  </span>
                    </div>
                </div>
            </div>
           
        </ItemTemplate>
    </asp:Repeater>
    <div style="clear: both;">
    </div>
</div>
<script type="text/javascript">
    $("img.lazy").lazyload({
        effect: "fadeIn"

    });
</script>

<asp:Panel ID="pnVideoList" runat="server" />

<%if (statusChannel == 0) {
    if (label == 1) {%>
    <p class="vi-view-more" id="pn_more">
    <a id="bt_cate_<%= cid %>_<%=type %>" class="vi-view-more-lnk" 
    href="javascript:loadmore_cate_channel_home('bt_cate_<%=Server.HtmlEncode(cid.ToString()) %>_<%=Server.HtmlEncode(type.ToString()) %>','<%= Server.HtmlEncode(pnVideoList.ClientID.ToString()) %>','ajax/moreVideoHome.aspx?lbs=<%=Server.HtmlEncode(lbs.ToString())%>&cid=<%=Server.HtmlEncode(cid.ToString()) %>&type=<%=Server.HtmlEncode(type.ToString())%>&p=<%=Server.HtmlEncode(p.ToString())%>',2)">
    </a> 
    </p>
<%} else {%>
    <p class="vi-view-more" id="p1">
    <a id="bt_channel_<%=Server.HtmlEncode(cid.ToString()) %>_<%=Server.HtmlEncode(type.ToString()) %>" class="vi-view-more-lnk" href="javascript:loadmore_cate_channel_home('bt_channel_<%=Server.HtmlEncode(cid.ToString()) %>_<%=Server.HtmlEncode(type.ToString()) %>','<%= Server.HtmlEncode(pnVideoList.ClientID.ToString()) %>','ajax/moreVideoChannel.aspx?lbs=<%=Server.HtmlEncode(lbs.ToString())%>&cid=<%=Server.HtmlEncode(cid.ToString()) %>&type=<%=Server.HtmlEncode(type.ToString())%>&p=<%=Server.HtmlEncode(p.ToString())%>',2)">
    </a> 
    </p>
<%} } %>

</div>
</div>
