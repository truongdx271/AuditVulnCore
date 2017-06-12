<%@ control language="C#" autoeventwireup="true" inherits="controls_UserName" CodeFile="UserName.ascx.cs"  %>
<div class="breadcrum-clip">
    VIDEO <span> ĐĂNG BỞI: <%=key%></span>
</div>
<br />
<asp:Panel ID="pnVideoList" runat="server" />
<p class="vi-view-more" id="pn_more">
    <a id="bt<%=Server.HtmlEncode(cid.ToString()) %>_<%=Server.HtmlEncode(type.ToString()) %>" class="vi-view-more-lnk" href="javascript:loadmore('bt<%=Server.HtmlEncode(cid.ToString()) %>_<%=Server.HtmlEncode(type.ToString()) %>','<%=Server.HtmlEncode(pnVideoList.ClientID.ToString()) %>','ajax/moreVideoByUserName.aspx?lbs=<%=Server.HtmlEncode(lbs.ToString()) %>&cid=<%=Server.HtmlEncode(cid.ToString()) %>&key=<%=Server.HtmlEncode(key.ToString()) %>&type=<%= Server.HtmlEncode(type.ToString()) %>&p=',2)" id="A1" >
    </a>
</p>