package br.com.emerson.repository;

import java.io.InputStream;
import java.util.UUID;

import javax.inject.Named;
import javax.jcr.Binary;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultDocumentRepository {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDocumentRepository.class);

    public String createData() throws Exception {

        Session session = getSession();

        try {
            Node root = session.getRootNode();

            // Store content
            Node hello = root.addNode("hi");
            Node world = hello.addNode("world");
            world.setProperty("message", "Hello, World!");
            session.save();

            // Retrieve content
            Node node = root.getNode("hi/world");
            System.out.println(node.getPath());
            System.out.println(node.getProperty("message").getString());

            return node.getPath() + " : " + node.getProperty("message").getString();
        } finally {
            session.logout();
        }
    }

    public String getData() throws Exception {
        Session session = getSession();

        try {
            Node root = session.getRootNode();

            Node node = root.getNode("hi/world");
            return node.getPath() + " : " + node.getProperty("message").getString();
        } catch (Exception e) {
            throw(e);
        } finally {
            session.logout();
        }
    }

    public void removeData() throws Exception {
        Session session = getSession();
        try {
            Node root = session.getRootNode();
            Node node = root.getNode("hi");
            node.remove();
            session.save();
        } catch (Exception e) {
            throw(e);
        } finally {
            session.logout();
        }
    }

    public InputStream getFile(String id) throws Exception {
        Session session = getSession();

        try {
            Property content = session.getNodeByIdentifier(id).getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA);
            InputStream inStream = content.getBinary().getStream();
            logger.debug("Get File");
            return inStream;
        } catch (Exception e) {
            throw(e);
        } finally {
            session.logout();
        }
    }

    public String createFile(InputStream inputStream) throws Exception {
        Session session = getSession();

        try {
            String uuid = UUID.randomUUID().toString();
            Node root = session.getRootNode();
            Node folderNode = getFolderRootNode(root, uuid);
            Node fileNode = folderNode.addNode(uuid, NodeType.NT_FILE);

            String fileId = fileNode.getIdentifier();

            Node contentNode = fileNode.addNode(Node.JCR_CONTENT, NodeType.NT_RESOURCE);

            Binary binary = session.getValueFactory().createBinary(inputStream);
            contentNode.setProperty(Property.JCR_DATA, binary);
            session.save();
            logger.debug("Create File");
            return fileId;
        } catch (Exception e) {
            throw(e);
        } finally {
            session.logout();
        }
    }

    public void deleteFile(String id) throws Exception {
        Session session = getSession();

        try {
            Node fileNode = session.getNodeByIdentifier(id);
            fileNode.remove();
            session.save();
            logger.debug("Remove file");
        } catch (Exception e) {
            throw(e);
        } finally {
            session.logout();
        }
    }

    private Node getFolderRootNode(Node root, String uuid) throws Exception {
        String[] values = uuid.split("-");
        Node level1Folder = getFolder(root, values[0]);
        Node level2Folder = getFolder(level1Folder, values[1]);
        return level2Folder;
    }

    private Session getSession() throws RepositoryException, LoginException {
        Repository repository = JcrUtils.getRepository();
        SimpleCredentials sc = new SimpleCredentials("admin", "admin".toCharArray());
        repository.login(sc);
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        return session;
    }

    private Node setFolder(Node root, String name) throws Exception {
        try {
            Node folderNode = null;
            if (!root.hasNode(name)) {
                folderNode = root.addNode(name, NodeType.NT_FOLDER);
            }
            return folderNode;
        } catch (Exception e) {
            throw(e);
        }
    }

    private Node getFolder(Node root, String name) throws Exception {
        try {
            Session session = getSession();
            Node folderNode = null;
            if (root.hasNode(name)) {
                folderNode = root.getNode(name);
                NodeType currentNodeType = folderNode.getPrimaryNodeType();
                NodeType folderNodeType = session.getWorkspace().getNodeTypeManager().getNodeType(NodeType.NT_FOLDER);
                if (currentNodeType != folderNodeType) {
                    throw new ConstraintViolationException(String.format("Folder node with id '%s' has invalid type"));
                }
            } else {
                folderNode = setFolder(root, name);
            }

            return folderNode;
        } catch (Exception e) {
            throw(e);
        }
    }

}
