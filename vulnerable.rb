class UsersController < ApplicationController
  def search
    # Brakeman: SQL Injection
    @users = User.where("name LIKE '%#{params[:name]}%'")
  end

  def execute_command
    # Brakeman: Command Injection
    system("echo #{params[:cmd]}")
  end

  def redirect_user
    # Brakeman: Open Redirect
    redirect_to params[:url]
  end

  def render_html
    # Brakeman: Cross-Site Scripting (XSS) / Unsafe Render
    render html: "<div>#{params[:content]}</div>".html_safe
  end
end
