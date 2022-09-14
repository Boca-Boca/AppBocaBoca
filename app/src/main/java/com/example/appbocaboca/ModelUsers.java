package com.example.appbocaboca;

import android.graphics.ColorSpace;

public class ModelUsers {

    // Usar o mesmo nome que no firebase database


    public ModelUsers(String nome, String email, String search, String telefone, String imagem, String cover, String uid) {
        this.nome = nome;
        this.email = email;
        this.search = search;
        this.telefone = telefone;
        this.imagem = imagem;
        this.cover = cover;
        this.uid = uid;
    }

    String nome;
    String email;
    String search;
    String telefone;
    String imagem;
    String cover;
    String uid;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ModelUsers(){


    }

}
